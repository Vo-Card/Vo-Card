package com.voc.config;

import com.voc.jwt.JwtManager;
import com.voc.security.AuthManager;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.voc.utils.AnsiColor.TAG_ERROR;
import static com.voc.utils.AnsiColor.TAG_SUCCESS;

import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {

        String authHeader = request.getHeader("Authorization");
        String currentPage = request.getRequestURI();

        // Assume no session exists by default
        boolean hasSession = false;

        // Check for JWT authentication first
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Optional<Long> optionalUserId = JwtManager.validateJwt(token);
            if (optionalUserId.isPresent()) {
                hasSession = true;
            }
        }

        // If no JWT, check for a valid session cookie and not API request
        if (!hasSession && !currentPage.startsWith("/api/")) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("session_token".equals(cookie.getName())) {
                        String[] parts = cookie.getValue().split(":");
                        if (parts.length == 2) {
                            String sessionId = parts[0];
                            String rawRefreshToken = parts[1];
                            if (AuthManager.validateSession(sessionId, rawRefreshToken)) {
                                System.out.println(TAG_SUCCESS + "Successfully to validate user session token");
                                hasSession = true;
                            } else {
                                System.out.println(TAG_ERROR + "Failed to validate user session token");
                            }
                        }
                    }
                }
            }
        }

        // Set the attribute based on the result of the checks
        if (hasSession) {
            request.setAttribute("hasSession", true);
        }

        // Now, handle excluded and protected pages
        String[] excludedPages = {
                "/home", "/login", "/register", "/css/**", "/js/**", "/fonts/**",
                "/resources/**", "/api/auth/**", "/about", "/contact", "/error/**",
                "/components/template/**", "/support/**"
        };
        for (String page : excludedPages) {
            if (currentPage.matches(page.replace("**", ".*"))) {
                return true;
            }
        }

        // If the page is not excluded, it must have a session
        if (!hasSession) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            if (currentPage.startsWith("/api/")) {
                System.out.println(TAG_ERROR + "The user is missing JWT key");
                response.getWriter().write("JWT is invalid or missing.");
            } else {
                System.out.println(TAG_ERROR + "The user doesnt have permission for this page");
                System.out.println(TAG_ERROR + "User cookie : " + request.getCookies());
                response.sendRedirect("/login");
            }
            return false;
        }

        return true;
    }
}