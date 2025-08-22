package com.voc.database;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.voc.helper.Row;

import org.mindrot.jbcrypt.BCrypt;

/**
 * <h1>Database security handler</h1>
 * The Security class is use to validate, manage
 * and interact with the database tables.
 * 
 * @author  Zartex <zartexvertagen@proton.me>
 * @version 0.0.1a
 * @since   2025-08-22
 */
public class Security {

    //------------------------------------------//
    //------------- PUBLIC METHODS -------------//
    //------------------------------------------//

    /**
     * <h2>Register User</h2>
     * This method validate and adding the user into the corresponding data table.
     * @param  displayName Get user's displayname
     * @param  username Get user's username
     * @param  password Get user's password
     * @param  confirmedPassword Use to confirm user password
     * @return true if user successfully register and false if they're not.
     */
    public static boolean registerUser(
        String displayName, 
        String username, 
        String password) {
        if (!isUserExist(username)) {
            String encryptedPassword = generateSecretKey(password);
            String sql = "INSERT INTO usertb (display_name, username, password) VALUES (?, ?, ?)";
            sqlPrepareStatement(sql, displayName, username, encryptedPassword);
            return true;
        }
        return false;
    }

    /**
     * This method validate user and give the user
     * session_id as a cookie which will be using for 
     * keeping the user always logged in.
     * @param username
     * @param password
     * @param browser_ip
     * @param browser_meta_data
     * @return Session key or null
     * @throws SQLException
     */
    public static String loginUser(
        String username,
        String password,
        String browser_ip,
        String browser_meta_data) {

        // Fetch user row
        String sql = "SELECT user_id_PK, password FROM usertb WHERE username = ?";
        Row user = getOneRow(sql, username);

        // Return null if user doesn't exist
        if (user == null) return null;

        Long userId = ((Number) user.get("user_id_PK")).longValue();
        String storedPassword = (String) user.get("password");

        // Verify password
        if (!verifyPassword(password, storedPassword)) return null;

        // Generate session and save
        try {
            String sessionId = generateUserSessionToken(userId, username, storedPassword);
            sql = "INSERT INTO sessiontb (session_id_PK, session_login_ip, session_browser_info, user_id_FK) VALUES (?, ?, ?, ?)";
            sqlPrepareStatement(sql, sessionId, browser_ip, browser_meta_data, userId);
            return sessionId;
        } catch (Exception e) {
            System.out.println("Encryption error: " + e.getMessage());
            return null;
        }
    }

    // TODO: add a proper commentation
    public static Row validateUserSession(String session_id){
        Row user_data = getUserDataFromSessionID(session_id);
        
        if (user_data != null){
            Long user_id = ((Number) user_data.get("user_id_PK")).longValue();
            String username = (String) user_data.get("username");
            String storedPassword = (String) user_data.get("password");

            String decrypted_session = null;
            try {
                decrypted_session = decryptUserSessionToken(session_id, storedPassword);
            } catch (Exception e) {
                System.out.println("Decryption error: " + e.getMessage());
                return null;
            }

            String[] decrypted_data = decrypted_session.split(":");

            if (decrypted_data.length < 2) return null;

            Long session_user_id = Long.parseLong(decrypted_data[0]);
            String session_username = decrypted_data[1];

            if (user_id.equals(session_user_id) && username.equals(session_username)) {
                return user_data;
            } else {
                return null;
            }
        }
        return null;
    }

    //-------------------------------------------//
    //------------- PRIVATE METHODS -------------//
    //-------------------------------------------//

    /**
     * Fetch user data associated with a session ID.
     * @param session_id the session ID to look up
     * @return Row containing user_id_PK, username, password; or null if session not found
     */
    private static Row getUserDataFromSessionID(String session_id) {
        String sql = "SELECT u.user_id_PK, u.username, u.password " +
                    "FROM sessiontb s " +
                    "JOIN usertb u ON s.user_id_FK = u.user_id_PK " +
                    "WHERE s.session_id_PK = ?";

        Row userData = getOneRow(sql, session_id);
        return userData;
    }

    /**
     * <h3>Generate User Session Token</h3>
     * <p>private static String generateUserSessionToken(Long user_id, String username, String password) throws Exception</p>
     * <p>Returns a complete encrypted session ID as a String.</p>
     * @param user_id Get user's id
     * @param username
     * @param password
     * @return 
     * @throws Exception
     */
    private static String generateUserSessionToken(Long user_id, String username, String password) throws Exception {
        // Derive key from password (SHA-256)
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(password.getBytes());
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

        // Generate random session component
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        String sessionRandom = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        // Create compound string
        String compound = user_id + ":" + username + ":" + sessionRandom;

        // Encrypt with AES
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // simpler than GCM for per-user key
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(compound.getBytes());

        return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);
    }


    // TODO: add a proper comment
    // Decrypt session token
    private static String decryptUserSessionToken(String token, String password) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(password.getBytes());
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decoded = Base64.getUrlDecoder().decode(token);
        byte[] decrypted = cipher.doFinal(decoded);

        return new String(decrypted); // returns "userId:username:sessionRandom"
    }

    /**
     * Generate a secure bcrypt hash from a plaintext password.
     * @param unencrypted The plaintext password
     * @return The hashed password (60 characters long)
     */
    private static String generateSecretKey(String unencrypted){
        return BCrypt.hashpw(unencrypted, BCrypt.gensalt(12));
    }

    /**
     * This method use to check if
     * the user exist or not.
     * @param username Check username
     * @return True if user exist
     */
    private static boolean isUserExist(String username){
        String sql = "SELECT COUNT(*) AS total FROM usertb WHERE username = ?";
        Row result = getOneRow(sql, username);

        if (result != null){
            Number countNum = (Number) result.get("total");
            if (countNum != null) {
                return countNum.longValue() > 0;
            }
        }

        return false;
    }

    /**
     * Verify a plaintext password against the stored bcrypt hash.
     * @param unencrypted The plaintext password
     * @param hashed The stored bcrypt hash
     * @return True if the password matches
     */
    public static boolean verifyPassword(String unencrypted, String hashed) {
        return BCrypt.checkpw(unencrypted, hashed);
    }

    /**
     * <h3>SQL Prepare Statement Helper</h3>
     * <p>private static ResultSet sqlPrepareStatement(String sql, Object... args)</p>
     * 
     * This method is a helper to handle the SQL execution and also handle the {@code SQLException}
     * @param sql SQL execution script.
     * @param args Arguments that need to be fill in order.
     * @return The result of the execution or null if executeUpdate
     */
    private static List<Row> sqlPrepareStatement(String sql, Object... args) {
        List<Row> resultList = new ArrayList<>();

        try (
            Connection connection = DatabaseUtils.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql);
        ) {
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
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }

        return resultList;
    }

    private static Row getOneRow(String sql, Object... args) {
        List<Row> rows = sqlPrepareStatement(sql, args);
        return rows.isEmpty() ? null : rows.get(0);
    }
}