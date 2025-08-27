package com.voc.security;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for generating secure random passwords.
 * <p>
 * This generator ensures that each password contains at least one
 * uppercase letter, one lowercase letter, one digit, and one special
 * character. The remaining characters are randomly selected from a
 * combined pool of all character categories. The final password is
 * shuffled to avoid predictable placement of guaranteed characters.
 * </p>
 *
 * <p><b>Example usage:</b></p>
 * <pre>
 *     String password = PasswordGenerator.generatePassword(12);
 *     System.out.println(password); // e.g., "A9x!qTrz2#Wf"
 * </pre>
 */
public class PasswordGenerator {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?";

    private static final String ALL = UPPERCASE + LOWERCASE + DIGITS + SPECIAL;

    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a secure random password of the specified length.
     * <p>
     * The password will always include at least one uppercase letter,
     * one lowercase letter, one digit, and one special character.
     * The remaining characters are randomly selected from the combined
     * character pool.
     * </p>
     *
     * @param length the desired length of the password (must be ≥ 8)
     * @return a randomly generated password string
     * @throws IllegalArgumentException if {@code length} is less than 8
     */
    public static String generatePassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8");
        }

        List<Character> passwordChars = new ArrayList<>();

        // Guarantee at least one from each category
        passwordChars.add(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        passwordChars.add(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        passwordChars.add(DIGITS.charAt(random.nextInt(DIGITS.length())));
        passwordChars.add(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // Fill the rest with random characters from the full set
        for (int i = 4; i < length; i++) {
            passwordChars.add(ALL.charAt(random.nextInt(ALL.length())));
        }

        // Shuffle to avoid predictable placement
        Collections.shuffle(passwordChars, random);

        // Build final string
        StringBuilder sb = new StringBuilder();
        for (char c : passwordChars) {
            sb.append(c);
        }

        return sb.toString();
    }
}
