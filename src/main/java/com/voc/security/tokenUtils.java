package com.voc.security;

import org.apache.commons.codec.digest.HmacUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class tokenUtils {
    private static final String SECRET_KEY = "UtzPuuGAx:8}4Hh]lFGB:Iw;+Ne`}4g|<S)Dlk78KXCe_fFgu(#@:aq3)$P!+iE";

    public static String generateToken(String username, int userId, String password) {
        String userData = base64Encode(username) + ":" + base64Encode(String.valueOf(userId));
        String base64 = base64Encode(userData);
        String mac = sign(base64, SECRET_KEY + password);
        return base64 + "." + mac;
    }

    public static boolean validateToken(String token, String password) {
        String[] parts = token.split("\\.");
        if (parts.length != 2) return false;
        String base = parts[0];
        String providedHash = parts[1];

        return sign(base, SECRET_KEY+password).equals(providedHash);
    }

    public static String getUsernameFromToken(String token) {
        String userData = splitToken(token);
        if (userData == null) return null;
        String[] parts = userData.split(":");
        if (parts.length != 2) return null;
        return base64Decode(parts[0]);
    }

    public static int getUserIdFromToken(String token) {
        String userData = splitToken(token);
        if (userData == null) return -1;
        String[] parts = userData.split(":");
        if (parts.length != 2) return -1;
        try {
            return Integer.parseInt(base64Decode(parts[1]));
        } catch (NumberFormatException e) {
            return -1; // Invalid user ID format
        }
    }

    public static String getTokenKey(String token) {
        String userData = splitToken(token);
        if (userData == null) return null;
        String[] parts = userData.split(":");
        if (parts.length != 2) return null;
        return base64Decode(parts[1]);
    }

    private static String splitToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 2) return null;
        return base64Decode(parts[0]);
    }

    private static String base64Encode(String data) {return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));}
    private static String base64Decode(String base64) {return new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);}

    private static String sign(String data, String key) {return new HmacUtils("HmacSHA256", key).hmacHex(data);}
}