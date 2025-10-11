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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voc.database.DatabaseUtils;
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

    private static class AssignUserRole {
        public Long targetId;
        public Long roleId;
    }

    private static class RoleUpdateRequest {
        public String roleId;
        public String roleName;
        public String roleDesc;
        public String bitmask;
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

    @GetMapping("/sessions")
    public ResponseEntity<Map<String, Object>> getSessions(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @NonNull HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);

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
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);

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
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);

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

            if (userId.get().equals(DatabaseUtils.getRootUserId())) {
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
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);

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
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
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
                System.out.println(getUserPerPage);
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
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
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

            if (target.equals(DatabaseUtils.getRootUserId())) {
                response.put("message", "You are not allow to do that.");
                return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT.value()).body(response);
            }

            boolean isSuccess = Permission.checkUserPermission(userId.get(), Values.DELETE_USER);

            if (isSuccess) {
                Boolean isComplete = UserManager.removeUser(target, null, true);

                if (isComplete) {
                    response.put("message", "Remove user sucessfully");
                    Permission.moderationAutoLog(userId.get(), "Force Delete User", "User deleted user");
                    return ResponseEntity.ok(response);
                }
            }
        }
        return null;
    }

    @DeleteMapping("/deleteRole")
    public ResponseEntity<Map<String, Object>> deleteRoles(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @NonNull HttpServletRequest request,
            @RequestParam(required = false) Long target) {

        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);

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

            if (Permission.getRootUserPermissionId().equals(target)) {
                response.put("Message", "You're not allow to do that.");
                return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT.value()).body(response);
            }

            boolean isSuccess = Permission.checkUserPermission(userId.get(), Values.ROOT_USER);

            if (isSuccess) {
                Boolean isComplete = Permission.removeRole(userId.get(), target);
                if (isComplete) {
                    Permission.moderationAutoLog(userId.get(), "Delete role", "User deleted role");
                    response.put("message", "Remove role sucessfully");
                    return ResponseEntity.ok(response);
                }
            }
        }
        return null;
    }

    @GetMapping("/listRole")
    public ResponseEntity<Map<String, Object>> listRole(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @NonNull HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
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

            }
        }
        return null;
    }

    @PutMapping("/assignRole")
    public ResponseEntity<Map<String, Object>> assignRoleUser(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @NonNull HttpServletRequest request,
            @RequestBody AssignUserRole req) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
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

            Long target = req.targetId;
            Long role = req.roleId;

            if (target.equals(DatabaseUtils.getRootUserId())) {
                response.put("message", "You are not allow to do this.");
                return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT.value()).body(response);
            }
            System.out.println("is null?");
            if (role == null) {
                boolean isSuccess = Permission.checkUserPermission(userId.get(), Values.ROOT_USER);
                if (isSuccess) {
                    Permission.updateUserRole(target, null, userId.get());
                    response.put("Success", "Complete assigned role");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("message", "Failed to remove role");
                    return ResponseEntity.ok(response);
                }
            }
            System.out.println("Pass");
            if (role.equals(Permission.getRootUserPermissionId())) {
                response.put("message", "You are not allow to do this.");
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE.value()).body(response);
            }

            System.out.println(TAG_DEBUG + "Target :" + target);

            boolean isSuccess = Permission.checkUserPermission(userId.get(), Values.ROOT_USER);

            if (isSuccess) {
                Permission.updateUserRole(target, role, userId.get());
                response.put("Success", "Complete assigned role");
                return ResponseEntity.ok(response);
            }
        }
        return null;
    }

    @GetMapping("/viewRoles")
    public ResponseEntity<Map<String, Object>> viewRoles(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @NonNull HttpServletRequest request,
            @RequestParam(required = false) Long page) {
        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
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
                List<Row> getRolePerPage = UserManager.getAllRole();
                response.put("Roles", getRolePerPage);
                return ResponseEntity.ok(response);
            }
        }
        return null;
    }

    @GetMapping("/currentRole")
    public ResponseEntity<Map<String, Object>> currentRole(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @NonNull HttpServletRequest request,
            @RequestParam(required = false) Long target) {

        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
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
                Row roleInfo = Permission.getCurrentRowInfo(target);
                response.put("role", roleInfo);
                return ResponseEntity.ok(response);
            }
        }
        return null;
    }

    @PutMapping("/updateRole")
    public ResponseEntity<Map<String, Object>> updateRole(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @NonNull HttpServletRequest request,
            @RequestBody RoleUpdateRequest req) {

        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
        if (userId.isPresent()) {
            String[] sessionToken = getUserSessionToken(request).split(":");
            String sessionid = sessionToken[0];
            String refreshToken = sessionToken[1];
            System.out.println(TAG_DEBUG + "Body :" + req);
            // Validate userssion :D
            if (!SessionManager.validateSessionToken(sessionid, refreshToken,
                    userId.get())) {
                // Logout the user since they are not authorize
                SessionManager.deleteSession(sessionid);
                response.put("message", "You are not authorized.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(response);
            }

            Long roleId = new java.math.BigInteger(req.roleId).longValue();
            Long bitmask = new java.math.BigInteger(req.bitmask).longValue();
            String name = req.roleName;
            String desc = req.roleDesc;

            // If the role is ROOT
            if (Permission.getRootUserPermissionId().equals(roleId)) {
                response.put("Message", "You're not allow to change permission on ROOT");
                return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT.value()).body(response);
            }

            boolean isSuccess = Permission.checkUserPermission(userId.get(), Values.ROOT_USER);

            if (isSuccess) {
                Permission.updateRole(roleId, name, bitmask, desc, userId.get());
                Permission.moderationAutoLog(userId.get(), "Update Role", "User updated " + name + " role");
                response.put("Message", "Role has been updated");
                return ResponseEntity.ok(response);
            }
        }
        return null;
    }

    @PostMapping("/createNewRole")
    public ResponseEntity<Map<String, Object>> createNewRole(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @NonNull HttpServletRequest request,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Long bitmask) {

        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
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

            if ((bitmask & Values.ROOT_USER) == 1) {
                // In case
                response.put("Message", "You can't create ROOT");
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE.value()).body(response);
            }

            boolean isSuccess = Permission.checkUserPermission(userId.get(), Values.ROOT_USER);

            if (isSuccess) {
                System.out.println(bitmask);
                System.out.println(roleName);
                Long isCreated = Permission.createRole(userId.get(), bitmask, roleName, null);
                response.put("Message", isCreated);
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Unable to create role"));
    }

    @GetMapping("/modLog")
    public ResponseEntity<Map<String, Object>> getModerationLog(
            @RequestHeader(value = "Authorization", required = false) String authToken,
            @NonNull HttpServletRequest request,
            @RequestParam(required = false) Long page) {

        Map<String, Object> response = new HashMap<>();
        Optional<Long> userId = JwtManager.getUserIdFromJWT(authToken);
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

            boolean isSuccess = Permission.checkUserPermission(userId.get(), Values.MODERATE_EXPLORER);

            if (isSuccess) {
                List<Row> modList = Permission.getModerationLog(userId.get());
                response.put("actionList", modList);
                return ResponseEntity.ok(response);
            }
        }
        return null;
    }
}