package com.voc.database;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.voc.helper.Row;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Security provides user authentication, password hashing, 
 * and session management utilities for database interactions.
 * <p>
 * It handles user registration, login, session validation, and password encryption.
 * All methods use {@link DatabaseUtils} to interact with the database safely.
 * </p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * boolean success = Security.registerUser("John Doe", "johnd", "password123");
 * String sessionId = Security.loginUser("johnd", "password123", "127.0.0.1", "Chrome/116");
 * Row userData = Security.validateUserSession(sessionId);
 * </pre>
 * 
 * @author  Zartex
 * @version 0.0.1a
 * @since   2025-08-22
 */
public class Security {

    //------------------------------------------//
    //------------- PUBLIC METHODS -------------//
    //------------------------------------------//

    /**
     * Registers a new user in the database.
     * <p>
     * The method checks if the username already exists, 
     * encrypts the password with bcrypt, and inserts the user into the table.
     * </p>
     *
     * @param displayName The user's display name.
     * @param username The user's chosen username.
     * @param password The user's chosen password (plaintext).
     * @return true if registration succeeds; false if the username already exists.
     */
    public static boolean registerUser(String displayName, String username, String password) {
        if (!isUserExist(username)) {
            String encryptedPassword = generateSecretKey(password);
            String sql = "INSERT INTO usertb (display_name, username, password) VALUES (?, ?, ?)";
            DatabaseUtils.sqlPrepareStatement(sql, displayName, username, encryptedPassword);
            return true;
        }
        return false;
    }

    /**
     * Authenticates a user and generates a session token.
     * <p>
     * The method verifies the password, generates a session ID,
     * and stores it in the database. Returns null if login fails.
     * </p>
     *
     * @param username The user's username.
     * @param password The user's plaintext password.
     * @param browser_ip The IP address of the user's browser.
     * @param browser_meta_data Metadata about the user's browser.
     * @return The session token if login succeeds; null otherwise.
     */
    public static String loginUser(
        String username,
        String password,
        String browser_ip,
        String browser_meta_data) {

        // Fetch user row
        String sql = "SELECT user_id_PK, password FROM usertb WHERE username = ?";
        Row user = DatabaseUtils.sqlSingleRowStatement(sql, username);

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
            DatabaseUtils.sqlPrepareStatement(sql, sessionId, browser_ip, browser_meta_data, userId);
            return sessionId;
        } catch (Exception e) {
            System.out.println("Encryption error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Validates a user's session token.
     * <p>
     * Checks if the session exists, decrypts the token, and verifies that it matches
     * the user ID and username. Returns user data if valid, otherwise null.
     * </p>
     *
     * @param session_id The session token to validate.
     * @return A {@link Row} containing user data if valid; null if invalid.
     */
    public static Row validateUserSession(String session_id) {
        Row user_data = getUserDataFromSessionID(session_id);

        if (user_data != null) {
            Long user_id = ((Number) user_data.get("user_id_PK")).longValue();
            String username = (String) user_data.get("username");
            String storedPassword = (String) user_data.get("password");

            String decrypted_session;
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

    /**
     * Verifies a plaintext password against a stored bcrypt hash.
     *
     * @param unencrypted The plaintext password.
     * @param hashed The stored bcrypt hash.
     * @return True if the password matches; false otherwise.
     */
    public static boolean verifyPassword(String unencrypted, String hashed) {
        return BCrypt.checkpw(unencrypted, hashed);
    }

    //-------------------------------------------//
    //------------- PRIVATE METHODS -------------//
    //-------------------------------------------//

    /**
     * Fetches user data associated with a session ID.
     *
     * @param session_id The session ID to look up.
     * @return A {@link Row} containing user_id_PK, username, password; or null if not found.
     */
    private static Row getUserDataFromSessionID(String session_id) {
        String sql = "SELECT u.user_id_PK, u.username, u.password " +
                     "FROM sessiontb s " +
                     "JOIN usertb u ON s.user_id_FK = u.user_id_PK " +
                     "WHERE s.session_id_PK = ?";

        return DatabaseUtils.sqlSingleRowStatement(sql, session_id);
    }

    /**
     * Generates an encrypted session token for a user.
     * <p>
     * Combines user ID, username, and a random component,
     * then encrypts with AES using the user's password as the key.
     * </p>
     *
     * @param user_id The user's ID.
     * @param username The user's username.
     * @param password The user's stored password.
     * @return Encrypted session token as a string.
     * @throws Exception If encryption fails.
     */
    private static String generateUserSessionToken(Long user_id, String username, String password) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(password.getBytes());
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        String sessionRandom = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        String compound = user_id + ":" + username + ":" + sessionRandom;

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(compound.getBytes());

        return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);
    }

    /**
     * Decrypts a session token to retrieve user information.
     *
     * @param token The encrypted session token.
     * @param password The user's stored password.
     * @return A string containing "userId:username:sessionRandom".
     * @throws Exception If decryption fails.
     */
    private static String decryptUserSessionToken(String token, String password) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(password.getBytes());
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decoded = Base64.getUrlDecoder().decode(token);
        byte[] decrypted = cipher.doFinal(decoded);

        return new String(decrypted);
    }

    /**
     * Generates a secure bcrypt hash from a plaintext password.
     *
     * @param unencrypted The plaintext password.
     * @return The hashed password (60 characters long).
     */
    private static String generateSecretKey(String unencrypted) {
        return BCrypt.hashpw(unencrypted, BCrypt.gensalt(12));
    }

    /**
     * Checks if a username already exists in the database.
     *
     * @param username The username to check.
     * @return True if the username exists; false otherwise.
     */
    private static boolean isUserExist(String username) {
        String sql = "SELECT COUNT(*) AS total FROM usertb WHERE username = ?";
        Row result = DatabaseUtils.sqlSingleRowStatement(sql, username);

        if (result != null) {
            Number countNum = (Number) result.get("total");
            if (countNum != null) {
                return countNum.longValue() > 0;
            }
        }
        return false;
    }
}
