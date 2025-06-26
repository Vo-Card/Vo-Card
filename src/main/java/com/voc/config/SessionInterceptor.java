package com.voc.config;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

@Configuration
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        String auth_token = null;

        System.out.println("SessionInterceptor: Checking session...");

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("auth_token".equals(cookie.getName())) {
                    auth_token = cookie.getValue();
                    break;
                }
            }
        }

        if (auth_token != null && isValid(auth_token)) {
            return true;
        }

        response.sendRedirect("/login"); // Block access
        return false;
    }

    private boolean isValid(String token) {
        return true; // Replace with real check
    }
}
