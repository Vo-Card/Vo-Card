package com.voc.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import com.voc.database.DatabaseUtils;
import com.voc.utils.Row;

import java.io.IOException;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JwtManager {

    private static final int JWT_EXP_MINUTES = 20;
    private static final int KEY_EXP_DAYS = 30;

    private static final Map<String, Key> activeKeys = new ConcurrentHashMap<>();
    private static volatile String primaryKid;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Initializes the key cache by loading all valid keys from the database.
     * This method should be called once at application startup.
     */
    public static void initializeKeys() {
        activeKeys.clear();
        String sql = "SELECT kid, secret_key, is_primary FROM jwt_keys WHERE expire_at > NOW() OR is_primary = 1";
        List<Row> rows = DatabaseUtils.sqlPrepareStatement(sql);

        if (rows != null) {
            for (Row row : rows) {
                String kid = (String) row.get("kid");
                String encodedSecret = (String) row.get("secret_key");
                boolean isPrimary = (boolean) row.get("is_primary");

                Key key = Keys.hmacShaKeyFor(Base64.getUrlDecoder().decode(encodedSecret));
                activeKeys.put(kid, key);

                if (isPrimary) {
                    primaryKid = kid;
                }
            }
        }

        if (activeKeys.isEmpty()) {
            rotateKey();
        }
    }

    /**
     * Generates and stores a new primary signing key.
     * This method is synchronized to prevent race conditions during key rotation.
     */
    public static synchronized void rotateKey() {
        DatabaseUtils.sqlPrepareStatement("UPDATE jwt_keys SET is_primary = 0 WHERE is_primary = 1");

        String kid = UUID.randomUUID().toString();
        Key newKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String encodedSecret = Base64.getUrlEncoder().withoutPadding().encodeToString(newKey.getEncoded());
        Instant expireAt = Instant.now().plus(KEY_EXP_DAYS, ChronoUnit.DAYS);

        DatabaseUtils.sqlPrepareStatement(
            "INSERT INTO jwt_keys (kid, secret_key, expire_at, is_primary) VALUES (?, ?, ?, 1)",
            kid, encodedSecret, Date.from(expireAt)
        );

        initializeKeys();
    }

    /**
     * Creates a new JWT using the current primary key.
     * The method includes a check to ensure the key will not expire during the JWT's lifetime.
     *
     * @param userId The user ID to embed in the JWT.
     * @param username The username to embed in the JWT.
     * @return A signed JWT string.
     */
    public static String signJwt(String userId, String username) {
        if (primaryKid == null) {
            rotateKey();
        } else {
            Row primaryKeyRow = DatabaseUtils.sqlSingleRowStatement(
                "SELECT expire_at FROM jwt_keys WHERE kid = ?", primaryKid
            );
            if (primaryKeyRow != null) {
                Date expireAt = (Date) primaryKeyRow.get("expire_at");
                if (expireAt.toInstant().isBefore(Instant.now().plus(JWT_EXP_MINUTES, ChronoUnit.MINUTES))) {
                    rotateKey();
                }
            } else {
                rotateKey();
            }
        }

        Key signingKey = activeKeys.get(primaryKid);
        Instant now = Instant.now();

        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setHeaderParam("kid", primaryKid)
            .setSubject(userId)
            .claim("username", username)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(JWT_EXP_MINUTES, ChronoUnit.MINUTES)))
            .signWith(signingKey)
            .compact();
    }

    /**
     * Validates a JWT using its key ID and returns the user ID if valid.
     * This method is robust against various token-related exceptions.
     *
     * @param token The JWT to validate.
     * @return An Optional containing the user ID if the token is valid, otherwise an empty Optional.
     */
    public static Optional<Long> validateJwt(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return Optional.empty();
            }

            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
            Map<String, Object> headerMap = objectMapper.readValue(
                headerJson, new TypeReference<Map<String, Object>>() {}
            );

            String kid = (String) headerMap.get("kid");

            if (kid == null) {
                return Optional.empty();
            }

            Key verificationKey = activeKeys.get(kid);
            if (verificationKey == null) {
                return Optional.empty();
            }

            Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(verificationKey)
                .build()
                .parseClaimsJws(token);

            String subject = claims.getBody().getSubject();
            return Optional.ofNullable(subject).map(Long::parseLong);
        } catch (JwtException | IOException | NullPointerException e) {
            return Optional.empty();
        }
    }
}