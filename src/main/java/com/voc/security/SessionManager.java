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
    /**
     * <p>
     * After registeration or login the session and token will be create
     * and check the token is not expire during life time
     * </p>
     * 
     * @param userId
     * @param username
     * @param rememberMe
     * @param ipAddress
     * @param userAgent
     * @return new sessionid
     */
    public static Row createSession(Long userId, String username, String displayName, boolean rememberMe,
            String ipAddress,
            String userAgent) {
        // post userid, username, remember me, ip adrdress, user agent
        String sessionId = UUID.randomUUID().toString();
        String rawRefreshToken = UUID.randomUUID().toString();
        // encrypt refreshToken for safety
        String hashedRefreshToken = BCrypt.hashpw(rawRefreshToken, BCrypt.gensalt(12));
        // if rememberMe == true : user will get 30 days refreshed token (Session id)
        int daysToExpire = rememberMe ? 30 : 7;
        Instant expiresAt = Instant.now().plus(daysToExpire, ChronoUnit.DAYS);

        // add refreshToken, sessionId remember_me etc.(param) to sessiontb
        String sql = "INSERT INTO sessiontb (session_id_PK, user_id_FK, refresh_token_hash, remember_me, expires_at, ip_address, user_agent) VALUES (?, ?, ?, ?, ?, ?, ?)";
        DatabaseUtils.sqlPrepareStatement(sql, sessionId, userId, hashedRefreshToken, rememberMe, Date.from(expiresAt),
                ipAddress, userAgent);
        // checking user Jwt key
        String accessToken = JwtManager.signJwt(userId.toString(), username, displayName);

        Row sessionData = new Row();
        sessionData.put("session_id", sessionId);
        sessionData.put("access_token", accessToken);
        sessionData.put("refresh_token", rawRefreshToken);
        sessionData.put("max_age", daysToExpire * 24 * 60 * 60L);
        return sessionData;
    }

    /**
     * <p>
     * Checking session in database if session has been expired or session is null
     * if session is expired delete that session row then create new sessionid
     * </p>
     * 
     * @param sessionId
     * @param rawRefreshToken
     * @return new sessionid
     */
    public static Optional<Row> refreshSession(String sessionId, String rawRefreshToken) {
        // Step 1: Validate the session from the database.
        String sql = """
                    SELECT s.user_id_FK, s.refresh_token_hash, s.remember_me, s.ip_address, s.user_agent, u.username, u.display_name
                    FROM sessiontb s
                    JOIN usertb u ON s.user_id_FK = u.user_id_PK
                    WHERE s.session_id_PK = ? AND s.expires_at > NOW()
                """;
        Row sessionRow = DatabaseUtils.sqlSingleRowStatement(sql, sessionId);

        // if there any session row in table : return invalid if session is null
        if (sessionRow == null) {
            return Optional.empty();
        }

        String hashedToken = (String) sessionRow.get("refresh_token_hash");
        if (!BCrypt.checkpw(rawRefreshToken, hashedToken)) {
            String deleteSql = "DELETE FROM sessiontb WHERE session_id_PK = ?";
            DatabaseUtils.sqlPrepareStatement(deleteSql, sessionId);
            return Optional.empty(); // Invalid token.
        }
        // delete the expired session
        String deleteSql = "DELETE FROM sessiontb WHERE session_id_PK = ?";
        DatabaseUtils.sqlPrepareStatement(deleteSql, sessionId);

        Long userId = ((Number) sessionRow.get("user_id_FK")).longValue();
        String username = (String) sessionRow.get("username");
        String displayName = (String) sessionRow.get("display_name");
        boolean rememberMe = (boolean) sessionRow.get("remember_me");
        String ipAddress = (String) sessionRow.get("ip_address");
        String userAgent = (String) sessionRow.get("user_agent");
        // create new session for user
        return Optional.of(createSession(userId, username, displayName, rememberMe, ipAddress, userAgent));
    }

    /** Optionally delete a session (logout) */
    public static void deleteSession(String sessionId) {
        String sql = "DELETE FROM sessiontb WHERE session_id_PK = ?";
        DatabaseUtils.sqlPrepareStatement(sql, sessionId);
    }
}