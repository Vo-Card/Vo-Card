package com.voc.security;

import java.time.LocalDateTime;

import org.mindrot.jbcrypt.BCrypt;

import com.voc.database.DatabaseUtils;
import com.voc.utils.Row;

/**
 * AuthManager handles user authentication operations,
 * including registration, login, and session validation.
 * 
 * <p>
 * Uses PasswordUtils for secure password handling and SessionManager
 * for session token generation and validation.
 * </p>
 */
public class AuthManager {

    /**
     * Registers a new user in the database.
     * 
     * <p>
     * This method checks if the username already exists, and if not,
     * hashes the password and inserts a new user row.
     * </p>
     * 
     * @param displayName User's display name
     * @param username    User's username
     * @param password    User's plaintext password
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
     * @param username        User's username
     * @param password        User's plaintext password
     * @param browserIp       Browser IP of the client
     * @param browserMetaData Browser metadata
     * @return Session token if login succeeds, or null if authentication fails
     */
    public static Row loginUser(String username, String password, boolean rememberMe, String ipAddress, String userAgent) {
        // Fetch user
        String sql = "SELECT user_id_PK, password FROM usertb WHERE username = ?";
        Row user = DatabaseUtils.sqlSingleRowStatement(sql, username);

        if (user == null)
            return null;

        Long userId = ((Number) user.get("user_id_PK")).longValue();
        String storedPassword = (String) user.get("password");

        if (!PasswordUtils.verifyPassword(password, storedPassword))
            return null;

        try {
            Row sessionId = SessionManager.createSession(userId, username, rememberMe, ipAddress, userAgent);
            return sessionId;
        } catch (Exception e) {
            System.err.println("Encryption error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Checks if a session is valid by validating both the session ID and refresh token.
     *
     * @param sessionId       The session ID from the cookie.
     * @param rawRefreshToken The raw refresh token from the cookie.
     * @return true if the session is valid and the token matches, otherwise false.
     */
    public static boolean validateSession(String sessionId, String rawRefreshToken) {
        String sql = "SELECT refresh_token_hash FROM sessiontb WHERE session_id_PK = ? AND expires_at > ?";
        
        Row sessionData = DatabaseUtils.sqlSingleRowStatement(sql, sessionId, LocalDateTime.now());
        
        if (sessionData == null) {
            return false;
        }
        
        String hashedToken = (String) sessionData.get("refresh_token_hash");
        
        boolean isValid = BCrypt.checkpw(rawRefreshToken, hashedToken);
        return isValid;
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
            if (countNum != null)
                return countNum.longValue() > 0;
        }

        return false;
    }
}
