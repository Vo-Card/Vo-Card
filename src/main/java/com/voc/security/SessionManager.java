package com.voc.security;

import com.voc.database.DatabaseUtils;
import com.voc.utils.Row;
import com.voc.jwt.JwtManager; // Ensure this is the correct package for your JwtManager
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class SessionManager {

    /** Generate a new session with JWT access token and a refresh token. */
    public static Row createSession(Long userId, String username, boolean rememberMe, String ipAddress, String userAgent) {
        String sessionId = UUID.randomUUID().toString();
        String rawRefreshToken = UUID.randomUUID().toString();
        String hashedRefreshToken = BCrypt.hashpw(rawRefreshToken, BCrypt.gensalt(12));

        int daysToExpire = rememberMe ? 30 : 7;
        Instant expiresAt = Instant.now().plus(daysToExpire, ChronoUnit.DAYS);

        String sql = "INSERT INTO sessiontb (session_id_PK, user_id_FK, refresh_token_hash, remember_me, expires_at, ip_address, user_agent) VALUES (?, ?, ?, ?, ?, ?, ?)";
        DatabaseUtils.sqlPrepareStatement(sql, sessionId, userId, hashedRefreshToken, rememberMe, Date.from(expiresAt), ipAddress, userAgent);

        String accessToken = JwtManager.signJwt(userId.toString(), username);

        Row sessionData = new Row();
        sessionData.put("session_id", sessionId);
        sessionData.put("access_token", accessToken);
        sessionData.put("refresh_token", rawRefreshToken);
        sessionData.put("max_age", daysToExpire * 24 * 60 * 60L);
        return sessionData;
    }

    /**
     * Refresh an existing session. This includes refresh token rotation and sliding session expiration.
     * Returns a new JWT access token and a new refresh token.
     */
    public static Optional<Row> refreshSession(String sessionId, String rawRefreshToken) {
        // Step 1: Validate the session from the database.
        String sql = "SELECT user_id_FK, refresh_token_hash, remember_me, ip_address, user_agent FROM sessiontb WHERE session_id_PK = ? AND expires_at > NOW()";
        Row sessionRow = DatabaseUtils.sqlSingleRowStatement(sql, sessionId);

        if (sessionRow == null) {
            return Optional.empty();
        }

        String hashedToken = (String) sessionRow.get("refresh_token_hash");
        if (!BCrypt.checkpw(rawRefreshToken, hashedToken)) {
            String deleteSql = "DELETE FROM sessiontb WHERE session_id_PK = ?";
            DatabaseUtils.sqlPrepareStatement(deleteSql, sessionId);
            return Optional.empty(); // Invalid token.
        }

        String deleteSql = "DELETE FROM sessiontb WHERE session_id_PK = ?";
        DatabaseUtils.sqlPrepareStatement(deleteSql, sessionId);

        Long userId = (Long) sessionRow.get("user_id_FK");
        String username = (String) sessionRow.get("username");
        boolean rememberMe = (boolean) sessionRow.get("remember_me");
        String ipAddress = (String) sessionRow.get("ip_address");
        String userAgent = (String) sessionRow.get("user_agent");

        return Optional.of(createSession(userId, username, rememberMe, ipAddress, userAgent));
    }

    /** Optionally delete a session (logout) */
    public static void deleteSession(String sessionId) {
        String sql = "DELETE FROM sessiontb WHERE session_id_PK = ?";
        DatabaseUtils.sqlPrepareStatement(sql, sessionId);
    }
}