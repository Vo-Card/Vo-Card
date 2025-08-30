package com.voc.database;

import static com.voc.utils.AnsiColor.BLUE;
import static com.voc.utils.AnsiColor.BOLD;
import static com.voc.utils.AnsiColor.RESET;
import static com.voc.utils.AnsiColor.TAG_ALERT;
import static com.voc.utils.AnsiColor.TAG_DEBUG;
import static com.voc.utils.AnsiColor.TAG_ERROR;
import static com.voc.utils.AnsiColor.TAG_IMPORTANT;
import static com.voc.utils.AnsiColor.TAG_INFO;
import static com.voc.utils.AnsiColor.TAG_SUCCESS;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.voc.security.AuthManager;
import com.voc.security.PasswordGenerator;
import com.voc.utils.Row;

/**
 * <p>
 * DatabaseUtils provides essential SQL execution utilities and database
 * initialization for your application.
 * It handles SQL exceptions gracefully by printing errors instead of throwing
 * stack traces.
 * </p>
 *
 * <p>
 * <b>Note:</b> If your database account does not have permission to create or
 * edit the database,
 * manual intervention is required.
 * </p>
 *
 * <p>
 * DatabaseUtils loads your database configuration from
 * {@code src/main/resources/config/database.json}. If the file does not exist,
 * create one using this template:
 * </p>
 * 
 * <pre>
 * {
 *     "DB_URL" : "jdbc:mysql://localhost:3306/",
 *     "DB_NAME" : "YOUR_TABLE_NAME",
 *     "DB_USER" : "YOUR_DATABASE_USERNAME",
 *     "DB_PASSWORD" : "YOUR_DATABASE_PASSWORD",
 *     "CHECK_DATABASE": "true"
 * }
 * </pre>
 *
 * <p>
 * SQL error handlers in DatabaseUtils return {@code null} for failed
 * connections or queries,
 * while printing relevant error messages to the console.
 * </p>
 */
public class DatabaseUtils {

    // Class-global database configuration
    private static String DB_URL;
    private static String DB_NAME;
    private static String DB_USER;
    private static String DB_PASSWORD;

    private static String ROOT_USERNAME;
    private static String ROOT_DISPLAYNAME;

    /**
     * Initializes database configuration from a map of settings.
     * <p>
     * This method reads database connection parameters and the root user
     * credentials from the provided map.
     * If {@code CHECK_DATABASE} is set to {@code true}, it verifies the
     * database connection and initializes the database if necessary.
     * </p>
     * <p>
     * The root user is created only if it does not already exist in the
     * database.
     * </p>
     * @param data
     */
    public static void initDatabase(Map<String, String> data) {
        System.out.println("[" + BOLD + BLUE + "VO-CARD" + RESET + "] Entering initDatabase");

        DB_URL = data.get("DB_URL");
        DB_NAME = data.get("DB_NAME");
        DB_USER = data.get("DB_USER");
        DB_PASSWORD = data.get("DB_PASSWORD");
        ROOT_USERNAME = data.get("ROOT_USERNAME").toLowerCase();
        ROOT_DISPLAYNAME = data.get("ROOT_DISPLAYNAME");

        // Check and initialize database if configured
        boolean dbOk = checkDatabase();
        System.out.println(TAG_DEBUG + "Database check result: " + (dbOk ? "OK" : "FAILED"));
    }

    public static String getRootUsername() {
        return ROOT_USERNAME;
    }

    /**
     * Initializes the administrator (root) user for the system.
     * <p>
     * This method generates a secure random password for the root user and
     * registers the account in the authentication system. The root password
     * is generated only once immediately after the database is created and
     * will not be displayed again.
     * </p>
     * <p>
     * The generated credentials are printed to the console for the developer
     * or system administrator to record. It is the caller's responsibility
     * to store this password in a safe location.
     * </p>
     *
     * @implNote This method should only be called during the initial database
     *           setup process. Repeated calls will overwrite the root user's
     *           credentials.
     */
    public static void initializeAdministrator() {
        boolean dbOk = checkDatabase();

        if (dbOk) {
            Row row = sqlSingleRowStatement("SELECT * FROM usertb WHERE username = ?", ROOT_USERNAME);
            System.out.println(TAG_ALERT + "Root user exists? " + (row != null));

            if (row == null) {
                String rootPassword = PasswordGenerator.generatePassword(32);
                AuthManager.registerUser(ROOT_DISPLAYNAME, ROOT_USERNAME, rootPassword);
        
                Row result = sqlSingleRowStatement("SELECT user_id_PK FROM usertb WHERE username = ?", ROOT_USERNAME);
        
                System.out.println(TAG_IMPORTANT+"Root user has been initialize for this project.");
                System.out.println(TAG_IMPORTANT+"Root ID: " + (BigInteger) result.get("user_id_PK"));
                System.out.println(TAG_IMPORTANT+"Root Username: " + ROOT_USERNAME);
                System.out.println(TAG_IMPORTANT+"Root Password: " + rootPassword);
                System.out.println(TAG_IMPORTANT+"Please keep this password in a secure location.");
                System.out.println(TAG_IMPORTANT+"The password will show only once.");

                System.out.println(TAG_SUCCESS + "Root user initialized");
            }
        } else {
            System.err.println(TAG_ERROR + "Database not ready or check failed");
        }
    }

    /**
     * Attempts to open a database connection.
     * <p>
     * If the connection cannot be established, an error message is printed
     * and the method returns {@code null}.
     * </p>
     *
     * @return A Connection object if successful, or {@code null} if connection
     *         fails.
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            int code = e.getErrorCode();
            // Common MySQL/MariaDB error handling
            switch (code) {
                case 1045: // ER_ACCESS_DENIED_ERROR
                    System.err.println("Access denied: Check your username or password.");
                    break;
                case 1049: // ER_BAD_DB_ERROR
                    System.err.println("Database not found: The database '" + DB_NAME + "' does not exist.");
                    break;
                case 2003: // Cannot connect to server
                    System.err.println("Cannot connect to database server. Is it running?");
                    break;
                default:
                    System.err.println("Database connection error [" + code + "]: " + e.getMessage());
            }
            return null;
        }
    }

    /**
     * Initializes the database if it does not exist.
     * <p>
     * This method creates the database and calls {@link #initialTables()} to
     * set up tables.
     * </p>
     */
    private static void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                Statement stmt = connection.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println(TAG_SUCCESS + "Database initialized successfully.");

        } catch (SQLException e) {
            System.err.println(TAG_ERROR + "Error creating database: " + e.getMessage());
            return;
        }

        // Initialize tables after database creation
        initialTables();
    }

    /**
     * Initializes SQL tables for the application.
     * <p>
     * Reads the SQL statements from {@code schema.sql} in the resources folder
     * and executes them sequentially.
     * </p>
     */
    private static void initialTables() {
        try (Connection connection = getConnection();
                Statement statement = connection.createStatement();
                InputStream inputStream = DatabaseUtils.class.getClassLoader().getResourceAsStream("schema.sql");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                throw new FileNotFoundException("schema.sql not found in resources folder.");
            }

            System.out.println(TAG_INFO + "Loading and executing schema.sql...");

            // Read SQL file into a single script
            StringBuilder sqlScript = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sqlScript.append(line).append("\n");
            }

            // Split and execute statements
            String[] statements = sqlScript.toString().split(";");
            for (String sql : statements) {
                if (!sql.trim().isEmpty()) {
                    statement.executeUpdate(sql.trim());
                }
            }

            System.out.println(TAG_SUCCESS + "SQL file executed successfully.");

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading SQL file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

        /**
     * Executes a batch of inserts/updates in a single transaction.
     * 
     * @param sql        The SQL insert/update statement with placeholders.
     * @param batchArgs  List of argument arrays, each array corresponds to one row.
     * @param batchLimit Number of statements to execute per batch.
     * @throws SQLException
     */
    public static void sqlExecuteBatch(String sql, List<Object[]> batchArgs, int batchLimit) {
        if (batchArgs == null || batchArgs.isEmpty()) return;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            int count = 0;

            for (Object[] args : batchArgs) {
                for (int i = 0; i < args.length; i++) {
                    pstmt.setObject(i + 1, args[i]);
                }
                pstmt.addBatch();
                count++;

                if (count >= batchLimit) {
                    pstmt.executeBatch();
                    conn.commit();
                    count = 0;
                }
            }

            if (count > 0) {
                pstmt.executeBatch();
                conn.commit();
            }
        } catch (Exception e){
            System.err.println("SQL Batch err: " + e.getMessage());
        }
    }

    // Convenience overload with default batch size
    public static void sqlExecuteBatch(String sql, List<Object[]> batchArgs) {
        sqlExecuteBatch(sql, batchArgs, 500);
    }


    /**
     * Validates the database condition.
     * <p>
     * Checks whether the database exists and is accessible.
     * If not, it attempts to initialize it.
     * </p>
     *
     * @return {@code true} if the database is accessible, {@code false} otherwise.
     */
    public static boolean checkDatabase() {
        Connection connection = getConnection();
        if (connection == null) {
            System.err.println(TAG_ERROR + "Connection failed. Trying to initialize database...");
            initializeDatabase();
            connection = getConnection();
        }

        boolean isConnected = connection != null;
        boolean isEmpty = true;

        if (isConnected) {
            try {
                Row result = DatabaseUtils.sqlSingleRowStatement(
                    "SELECT COUNT(*) AS table_count FROM information_schema.tables WHERE table_schema = ?", DB_NAME
                );

                if (result != null && result.get("table_count") != null) {
                    isEmpty = ((Number) result.get("table_count")).longValue() == 0;
                }

                if (isEmpty) {
                    System.out.println(TAG_INFO + "Database is empty. Initializing...");
                    initializeDatabase();

                    result = DatabaseUtils.sqlSingleRowStatement(
                        "SELECT COUNT(*) AS table_count FROM information_schema.tables WHERE table_schema = ?", DB_NAME
                    );
                    if (result != null && result.get("table_count") != null) {
                        isEmpty = ((Number) result.get("table_count")).longValue() == 0;
                    }
                }

            } catch (Exception e) {
                System.err.println(TAG_ERROR + "Error checking database tables: " + e.getMessage());
            } finally {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (Exception e) {
                    System.err.println(TAG_ERROR + "Error closing connection in checkDatabase: " + e.getMessage());
                }
            }
        }

        return isConnected && !isEmpty;
    }

    /**
     * Executes a parameterized SQL statement safely.
     * <p>
     * Handles {@code SQLException} by printing the error message and ensuring
     * connections are closed automatically.
     * </p>
     *
     * <p>
     * Example usage:
     * </p>
     * ~
     * 
     * <pre>
     * String sql;
     * List&lt;Row&gt; result;
     *
     * sql = "INSERT INTO users (username, password) VALUES (?, ?)";
     * result = sqlPrepareStatement(sql, "random_user", "pass1234");
     *
     * sql = "SELECT * FROM users";
     * result = sqlPrepareStatement(sql);
     *
     * for (Row row : result) {
     *     Long user_id = ((Number) row.get()).longValue();
     *     String username = (String) row.get("username");
     *     String password = (String) row.get("password");
     * }
     * </pre>
     *
     * @param sql  The SQL statement to execute.
     * @param args Arguments to bind to the SQL statement placeholders, in order.
     * @return A List of Row objects containing the results of the query, or an
     *         empty list if no results.
     * @see com.voc.utils.Row
     * @see #sqlSingleRowStatement(String, Object...)
     */
    public static SQLResult sqlPrepareStatement(String sql, Object... args) {
        List<Row> resultList = new ArrayList<>();
        long generatedKey = -1;
        int affectedRows = 0;
        try (Connection connection = getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Bind parameters
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    pstmt.setObject(i + 1, args[i]);
                }
            }

            if (sql.trim().toUpperCase().startsWith("SELECT")) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();

                    while (rs.next()) {
                        Row row = new Row();
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(meta.getColumnName(i), rs.getObject(i));
                        }
                        resultList.add(row);
                    }
                }
            } else {
                affectedRows = pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedKey = rs.getLong(1);
                    }
                }
            }
            return new SQLResult(true, resultList, generatedKey, affectedRows, null);

        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
            return new SQLResult(false, null, generatedKey, affectedRows, e.getMessage());
        }

    }

    /**
     * Executes a SQL statement and returns the first result row.
     *
     * @param sql  The SQL statement to execute.
     * @param args Arguments to bind to the SQL statement placeholders.
     * @return The first Row of the result, or {@code null} if no results.
     * @see #sqlPrepareStatement(String, Object...)
     */
    public static Row sqlSingleRowStatement(String sql, Object... args) {
        SQLResult rows = sqlPrepareStatement(sql, args);
        if (rows.isSuccess()) {
            return rows.getData().isEmpty() ? null : rows.getData().get(0);
        }
        return null;
    }
}
