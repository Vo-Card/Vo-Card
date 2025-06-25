package com.voc.config;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        String sessionToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("session_token".equals(cookie.getName())) {
                    sessionToken = cookie.getValue();
                    break;
                }
            }
        }

        if (sessionToken != null && isValid(sessionToken)) {
            return true; // Continue to controller
        }

        response.sendRedirect("/login"); // Block access
        return false;
    }

    private boolean isValid(String token) {
        // TODO: Check token in DB or in-memory store
        // SELECT * FROM users WHERE session = ?
        return true; // Replace with real check
    }
}
