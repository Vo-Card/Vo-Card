package com.voc.api;

import static com.voc.utils.AnsiColor.TAG_DEBUG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voc.database.UserManager;
import com.voc.jwt.JwtManager;
import com.voc.security.Permission;
import com.voc.security.SessionManager;
import com.voc.security.Permission.Values;
import com.voc.utils.Row;

@RestController
@RequestMapping("/api/user")
public class UserApiController {
    // User-related API endpoints would go here

    private static class UserProfileBody {
        public String username;
        public String displayName;
        public String newPassword;
        public String confirmPassword;
    }

    private static String getUserSessionToken(@NonNull HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("session_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private static Optional<Long> getUserIdFromJWT(String authToken) {
        if (authToken != null && authToken.startsWith("Bearer ")) {
            String token = authToken.substring(7);
            return JwtManager.validateJwt(token);
        }
        return Optional.empty();
    }

    @GetMapping("/sessions")
    public ResponseEntity<Map<String, Object>> getSessions(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @NonNull HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            String[] sessionToken = getUserSessionToken(request).split(":");
            String sessionid = sessionToken[0];
            String refreshToken = sessionToken[1];

            // Validate userssion :D
            if (!SessionManager.validateSessionToken(sessionid, refreshToken,
                    userId.get())) {
                // Logout the user since they are not authorize
                SessionManager.deleteSession(sessionid);
                response.put("message", "You are not authorized.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
            }

            List<Row> sessions = SessionManager.getAllSessionFromUser(userId.get());

            if (!sessions.isEmpty()) {
                response.put("current_session_id", sessionid);
                response.put("user_sessions", sessions);
                return ResponseEntity.ok(response);
            }
            response.put("message", "Failed to query session, please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @NonNull HttpServletRequest request,
            @RequestBody UserProfileBody profile) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            String[] sessionToken = getUserSessionToken(request).split(":");
            String sessionid = sessionToken[0];
            String refreshToken = sessionToken[1];

            // Validate userssion :D
            if (!SessionManager.validateSessionToken(sessionid, refreshToken,
                    userId.get())) {
                // Logout the user since they are not authorize
                SessionManager.deleteSession(sessionid);
                response.put("message", "You are not authorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
            }

            // Rules validation
            if (profile.username == null && profile.newPassword == null && profile.displayName == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "No update changes."));
            }

            if (profile.username != null
                    && !Pattern.compile("^(?!.*_.*_)[a-z_]{3,20}$").matcher(profile.username).matches()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message",
                                "Username must be 3-20 characters long, contain only lowercase letters and underscores, and cannot contain consecutive underscores."));
            }

            if (profile.newPassword != null
                    && !Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@/$%^&*-]).{8,}$")
                            .matcher(profile.newPassword)
                            .matches()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message",
                                "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character."));
            }

            Boolean isComplete = UserManager.updateUserInfo(userId.get(),
                    profile.username, profile.displayName,
                    profile.newPassword,
                    profile.confirmPassword, sessionid);

            if (isComplete) {
                response.put("message", "Update user sucessfully");
                return ResponseEntity.ok(response);
            }
            response.put("message", "Failed to update user, please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<Map<String, Object>> deleteUser(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @NonNull HttpServletRequest request,
            @RequestBody UserProfileBody profile) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            String[] sessionToken = getUserSessionToken(request).split(":");
            String sessionid = sessionToken[0];
            String refreshToken = sessionToken[1];

            // Validate userssion :D
            if (!SessionManager.validateSessionToken(sessionid, refreshToken,
                    userId.get())) {
                // Logout the user since they are not authorize
                SessionManager.deleteSession(sessionid);
                response.put("message", "You are not authorized.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
            }

            if (Permission.isUserRoot(userId.get())) {
                response.put("message", "You are not allow to do that.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
            }

            Boolean isComplete = UserManager.removeUser(userId.get(), profile.confirmPassword, false);

            if (isComplete) {
                response.put("message", "Remove user sucessfully");
                return ResponseEntity.ok(response);
            }
            response.put("message", "Failed to delete user, please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }
    }

    @DeleteMapping("/sessions")
    public ResponseEntity<Map<String, Object>> deleteSession(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @NonNull HttpServletRequest request,
            @RequestParam String selectedSession) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = getUserIdFromJWT(authToken);

        if (userId.isPresent()) {
            String[] sessionToken = getUserSessionToken(request).split(":");
            String sessionid = sessionToken[0];
            String refreshToken = sessionToken[1];

            // Validate userssion :D
            if (!SessionManager.validateSessionToken(sessionid, refreshToken,
                    userId.get())) {
                // Logout the user since they are not authorize
                SessionManager.deleteSession(sessionid);
                response.put("message", "You are not authorized.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
            }

            Boolean isSuccess = SessionManager.deleteSession(selectedSession);

            if (isSuccess) {
                response.put("message", "Complete deleted session");
                return ResponseEntity.ok(response);
            }
            response.put("message", "Failed to delete session, please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
        } else {
            response.put("message", "JWT missing userData");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
        }
    }

    @GetMapping("/getUser")
    public ResponseEntity<Map<String, Object>> displayAllUser(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @NonNull HttpServletRequest request,
            @RequestParam(required = false) Long page) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = getUserIdFromJWT(authToken);
        if (userId.isPresent()) {
            String[] sessionToken = getUserSessionToken(request).split(":");
            String sessionid = sessionToken[0];
            String refreshToken = sessionToken[1];

            // Validate userssion :D
            if (!SessionManager.validateSessionToken(sessionid, refreshToken,
                    userId.get())) {
                // Logout the user since they are not authorize
                SessionManager.deleteSession(sessionid);
                response.put("message", "You are not authorized.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
            }

            boolean isSuccess = Permission.checkUserPermission(userId.get(), Values.VIEW_ALL_USER);

            if (isSuccess) {
                List<Row> getUserPerPage = UserManager.getAllUserByPage(page);
                response.put("User", getUserPerPage);
                return ResponseEntity.ok(response);
            }
        }
        return null;
    }

    @DeleteMapping("/forceDeleteUser")
    public ResponseEntity<Map<String, Object>> removeUserByMod(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @NonNull HttpServletRequest request,
            @RequestParam(required = false) Long target) {

        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = getUserIdFromJWT(authToken);
        if (userId.isPresent()) {
            String[] sessionToken = getUserSessionToken(request).split(":");
            String sessionid = sessionToken[0];
            String refreshToken = sessionToken[1];

            // Validate userssion :D
            if (!SessionManager.validateSessionToken(sessionid, refreshToken,
                    userId.get())) {
                // Logout the user since they are not authorize
                SessionManager.deleteSession(sessionid);
                response.put("message", "You are not authorized.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
            }

            if (Permission.isUserRoot(target)) {
                response.put("message", "You are not allow to do that.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
            }

            boolean isSuccess = Permission.checkUserPermission(userId.get(), Values.DELETE_USER);

            if (isSuccess) {
                Boolean isComplete = UserManager.removeUser(target, null, true);

                if (isComplete) {
                    response.put("message", "Remove user sucessfully");
                    return ResponseEntity.ok(response);
                }
            }
        }
        return null;
    }
    // TODO: Create empty role API include backend
    // TODO: load Role API include backend
    // TODO: Update empty role API include backend
    // TODO: Delete empty role API include backend

    @PostMapping("/createNewRole")
    public ResponseEntity<Map<String, Object>> createNewRole(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @NonNull HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = getUserIdFromJWT(authToken);
        if (userId.isPresent()) {
            String[] sessionToken = getUserSessionToken(request).split(":");
            String sessionid = sessionToken[0];
            String refreshToken = sessionToken[1];

            // Validate userssion :D
            if (!SessionManager.validateSessionToken(sessionid, refreshToken,
                    userId.get())) {
                // Logout the user since they are not authorize
                SessionManager.deleteSession(sessionid);
                response.put("message", "You are not authorized.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
            }

            boolean isSuccess = Permission.checkUserPermission(userId.get(), Values.ROOT_USER);

            if (isSuccess) {
                Long isCreated = Permission.createRole(userId.get(), Values.ROOT_USER, "New Role", null);
                response.put("Message", isCreated);
                return ResponseEntity.ok(response);
            }
        }
        return null;
    }
}