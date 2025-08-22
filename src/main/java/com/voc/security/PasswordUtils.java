package com.voc.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * PasswordUtils handles password hashing and verification using bcrypt.
 */
public class PasswordUtils {

    /**
     * Generates a secure bcrypt hash from a plaintext password.
     * 
     * @param plaintext Plaintext password
     * @return Bcrypt hashed password
     */
    public static String generateSecretKey(String plaintext) {
        return BCrypt.hashpw(plaintext, BCrypt.gensalt(12));
    }

    /**
     * Verifies a plaintext password against a bcrypt hash.
     * 
     * @param plaintext Plaintext password
     * @param hash Stored bcrypt hash
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String plaintext, String hash) {
        return BCrypt.checkpw(plaintext, hash);
    }
}
