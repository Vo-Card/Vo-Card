package com.voc.api;

import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voc.security.AuthManager;

/**
 * AuthApiController provides RESTful endpoints for user authentication.
 * <p>
 * It handles user registration and login via JSON requests and responses.
 * </p>
 * <p>
 * The controller validates input data, manages sessions, and returns appropriate
 * HTTP status codes and messages.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    /** Request body for user registration. */
    public static class RegisterRequest {
        public String displayName;
        public String username;
        public String password;
        public String confirmPassword;
    }

    /** Request body for user login. */
    public static class LoginRequest {
        public String username;
        public String password;
    }

    /**
    * Handles user registration.
    * <p>
    * Validates input data and creates a new user if valid.
    * </p>
    *
    * @param req The registration request body
    * @return A response entity with success or error message
    */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest req) {
        String displayName = req.displayName != null ? req.displayName.trim() : "";
        String username = req.username != null ? req.username.trim() : "";
        String password = req.password != null ? req.password.trim() : "";
        String confirmPassword = req.confirmPassword != null ? req.confirmPassword.trim() : "";

        // Rules validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username and/or password cannot be empty."));
        }

        if (!password.equals(confirmPassword)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Passwords do not match."));
        }

        if (!Pattern.compile("^(?!.*_.*_)[a-z_]{3,20}$").matcher(username).matches()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            "Username must be 3-20 characters long, contain only lowercase letters and underscores, and cannot contain consecutive underscores."));
        }

        if (!Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@/$%^&*-]).{8,}$").matcher(password)
                .matches()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character."));
        }

        // Process registration
        boolean registered = AuthManager.registerUser(displayName, username, password);

        if (registered) {
            return ResponseEntity.ok(Map.of("message", "Registration successful. Please login."));
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username already exists."));
        }
    }

    /**
    * Handles user login.
    * <p>
    * Validates credentials and creates a session if valid.
    * </p>
    * @param usr      The login request body
    * @param request  The HTTP request
    * @param response The HTTP response
    * @return A response entity with success or error message
    */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest usr,
            HttpServletRequest request,
            HttpServletResponse response) {
        String username = usr.username != null ? usr.username.trim() : "";
        String password = usr.password != null ? usr.password.trim() : "";

        if (username.isEmpty() || password.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username and/or password cannot be empty."));
        }

        String sessionId = AuthManager.loginUser(username, password, request.getHeader("X-Forwarded-For"),
                request.getHeader("User-Agent"));

        if (sessionId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username or Password is not Correct."));
        }
        Cookie cookie = new Cookie("session_id", sessionId);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.ok(Map.of("message", "Login successful."));
    }
}
