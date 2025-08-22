package com.voc.config;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import com.voc.database.Security;
import com.voc.helper.Row;

@Configuration
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Cookie[] cookies = request.getCookies();
        String session_token = null;
        // get current page URL
        String currentPage = request.getRequestURI();

        String[] excludedPages = { "/home", "/login", "/register", "/css/**", "/js/**", "/resources/**"};

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("session_id".equals(cookie.getName())) {
                    session_token = cookie.getValue();
                    break;
                }
            }
        }

        if (session_token != null) {
            Row user_data = Security.validateUserSession(session_token);
            if (user_data != null){
                request.setAttribute("username", (String) user_data.get("username"));
            }
            return true;
        }
        
        // Check if the current page is in the excluded pages
        for (String page : excludedPages) {
            if (currentPage.matches(page.replace("**", ".*"))) {
                return true;
            }
        }

        response.sendRedirect("/login");
        return false;
    }
}
