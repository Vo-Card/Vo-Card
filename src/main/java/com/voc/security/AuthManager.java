package com.voc.security;

import com.voc.database.DatabaseUtils;
import com.voc.helper.Row;

/**
 * AuthManager handles user authentication operations,
 * including registration, login, and session validation.
 * 
 * <p>Uses PasswordUtils for secure password handling and SessionManager
 * for session token generation and validation.</p>
 * 
 * @author Zartex
 * @version 0.0.1a
 * @since 2025-08-22
 */
public class AuthManager {

    /**
     * Registers a new user in the database.
     * 
     * <p>This method checks if the username already exists, and if not,
     * hashes the password and inserts a new user row.</p>
     * 
     * @param displayName User's display name
     * @param username User's username
     * @param password User's plaintext password
     * @return true if registration succeeded, false if username already exists
     */
    public static boolean registerUser(String displayName, String username, String password) {
        if (!isUserExist(username)) {
            String hashedPassword = PasswordUtils.generateSecretKey(password);
            String sql = "INSERT INTO usertb (display_name, username, password) VALUES (?, ?, ?)";
            DatabaseUtils.sqlPrepareStatement(sql, displayName, username, hashedPassword);
            return true;
        }
        return false;
    }

    /**
     * Logs in a user by validating credentials and creating a session.
     * 
     * @param username User's username
     * @param password User's plaintext password
     * @param browserIp Browser IP of the client
     * @param browserMetaData Browser metadata
     * @return Session token if login succeeds, or null if authentication fails
     */
    public static String loginUser(String username, String password, String browserIp, String browserMetaData) {
        // Fetch user
        String sql = "SELECT user_id_PK, password FROM usertb WHERE username = ?";
        Row user = DatabaseUtils.sqlSingleRowStatement(sql, username);

        if (user == null) return null;

        Long userId = ((Number) user.get("user_id_PK")).longValue();
        String storedPassword = (String) user.get("password");

        if (!PasswordUtils.verifyPassword(password, storedPassword)) return null;

        try {
            String sessionId = SessionManager.generateUserSessionToken(userId, username, storedPassword);
            sql = "INSERT INTO sessiontb (session_id_PK, session_login_ip, session_browser_info, user_id_FK) VALUES (?, ?, ?, ?)";
            DatabaseUtils.sqlPrepareStatement(sql, sessionId, browserIp, browserMetaData, userId);
            return sessionId;
        } catch (Exception e) {
            System.err.println("Encryption error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Validates a session token and returns the associated user data.
     * 
     * @param sessionId The session token
     * @return User row if session is valid, null otherwise
     */
    public static Row validateUserSession(String sessionId) {
        Row userData = SessionManager.getUserDataFromSessionID(sessionId);

        if (userData != null) {
            Long userId = ((Number) userData.get("user_id_PK")).longValue();
            String username = (String) userData.get("username");
            String storedPassword = (String) userData.get("password");

            try {
                String decryptedSession = SessionManager.decryptUserSessionToken(sessionId, storedPassword);
                String[] parts = decryptedSession.split(":");
                if (parts.length < 2) return null;

                Long sessionUserId = Long.parseLong(parts[0]);
                String sessionUsername = parts[1];

                if (userId.equals(sessionUserId) && username.equals(sessionUsername)) {
                    return userData;
                }
            } catch (Exception e) {
                System.err.println("Decryption error: " + e.getMessage());
            }
        }

        return null;
    }

    /**
     * Checks if a username already exists in the database.
     * 
     * @param username Username to check
     * @return true if user exists, false otherwise
     */
    private static boolean isUserExist(String username) {
        String sql = "SELECT COUNT(*) AS total FROM usertb WHERE username = ?";
        Row result = DatabaseUtils.sqlSingleRowStatement(sql, username);

        if (result != null) {
            Number countNum = (Number) result.get("total");
            if (countNum != null) return countNum.longValue() > 0;
        }

        return false;
    }
}
