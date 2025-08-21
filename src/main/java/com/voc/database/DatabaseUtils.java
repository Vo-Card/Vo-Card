package com.voc.database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <h1>Manage Connection Database</h1>
 * The DatabaseUtils class is mainly use for manage 
 * the whole database.
 * (e.g. UserTable, DeckTable, Setting, ect.)
 * 
 * @author  ZartexV <zartexvertagen@proton.me>
 * @version 0.0.1a (GIT: ${git.commit.id.abbrev})
 * @since   2025-08-21
 */
public class DatabaseUtils {

    //Store class-global database information.
    private static String DB_URL;
    private static String DB_NAME;
    private static String DB_USER;
    private static String DB_PASSWORD;

    private static String ALWAYS_CHECK_DATABASE;

    static {
        // Attempting to load datbase config file
        try (InputStream input = DatabaseUtils.class.getClassLoader().getResourceAsStream("config/database.json")) {
            if (input == null) {
                throw new RuntimeException(
                    "database.json not found or haven't config. Please check the config folder in resources file."
                );
            }

            //Turn JSON file into Map.
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> data = mapper.readValue(input, new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});

            DB_URL = data.get("DB_URL");
            DB_NAME = data.get("DB_NAME");
            DB_USER = data.get("DB_USER");
            DB_PASSWORD = data.get("DB_PASSWORD");
            ALWAYS_CHECK_DATABASE = data.get("CHECK_DATABASE");

            if (ALWAYS_CHECK_DATABASE == "true") {
                checkDatabase();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method use to get a database connection.
     * @return Connection This return the connection to the database.
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            int code = e.getErrorCode();
            // Common MySQL/MariaDB error handling
            if (code == 1045) { // ER_ACCESS_DENIED_ERROR
                System.err.println("Access denied: Check your username or password.");
            } else if (code == 1049) { // ER_BAD_DB_ERROR
                System.err.println("Database not found: The database '" + DB_NAME + "' does not exist.");
            } else if (code == 2003) { // Can't connect to MySQL server
                System.err.println("Cannot connect to database server. Is it running?");
            } else {
                System.err.println("Database connection error [" + code + "]: " + e.getMessage());
            }
            return null;
        }
    }

    /**
     * This method use for initialize the database if it not exist.
     */
    private static void initializeDatabase() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = connection.createStatement();

            String sql = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            stmt.executeUpdate(sql);
            stmt.close();
            connection.close();

            System.out.println("Database initizialized successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating database: " + e.getMessage());
            return;
        }

        initialTables();
        return;
    }


    /**
     * Initialize the SQL tables for the webapp.
     */
    public static void initialTables() {
        try (Connection connection = getConnection();
            Statement statement = connection.createStatement();
            InputStream inputStream = DatabaseUtils.class.getClassLoader().getResourceAsStream("schema.sql");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                throw new FileNotFoundException("schema.sql not found in resources folder.");
            }

            System.out.println("Connected to the database.");

            // Read SQL file
            StringBuilder sqlScript = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sqlScript.append(line).append("\n");
            }

            // Execute SQL statements
            String[] statements = sqlScript.toString().split(";");
            for (String sql : statements) {
                if (!sql.trim().isEmpty()) {
                    statement.executeUpdate(sql.trim());
                }
            }

            System.out.println("SQL file executed successfully.");

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading SQL file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Validate the database condition
     */
    public static boolean checkDatabase() {
        Connection connection = getConnection();
        //Initialize if database is not exist
        if (connection == null){
            initializeDatabase();
            connection = getConnection();
        }

        return connection != null;
    }
}
