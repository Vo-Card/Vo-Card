package com.voc.security;

import com.voc.database.DatabaseUtils;
import com.voc.utils.Row;

import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * SessionManager handles session token generation and decryption.
 * All sessions are encrypted using per-user AES keys derived from password.
 * <p>
 * This ensures that even if the session store is compromised, session tokens
 * cannot be decrypted without the user's password.
 * </p>
 */
public class SessionManager {

    /**
     * Generates an encrypted session token for a user.
     * 
     * @param userId   User ID
     * @param username Username
     * @param password User password
     * @return Encrypted session token
     * @throws Exception Encryption errors
     */
    public static String generateUserSessionToken(Long userId, String username, String password) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(password.getBytes());
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        String sessionRandom = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        String compound = userId + ":" + username + ":" + sessionRandom;

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(compound.getBytes());

        return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);
    }

    /**
     * Decrypts a session token using the user's password.
     * 
     * @param token    Encrypted session token
     * @param password User password
     * @return Decrypted string in the form "userId:username:random"
     * @throws Exception Decryption errors
     */
    public static String decryptUserSessionToken(String token, String password) throws Exception {
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
     * Fetches user data associated with a session token.
     * 
     * @param sessionId Session token
     * @return User row or null if session not found
     */
    public static Row getUserDataFromSessionID(String sessionId) {
        String sql = "SELECT u.user_id_PK, u.username, u.password " +
                "FROM sessiontb s " +
                "JOIN usertb u ON s.user_id_FK = u.user_id_PK " +
                "WHERE s.session_id_PK = ?";
        return DatabaseUtils.sqlSingleRowStatement(sql, sessionId);
    }
}
