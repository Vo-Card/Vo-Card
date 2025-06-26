package com.voc.config;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import com.voc.database.dbUtils;
import com.voc.database.userUtils;

@Configuration
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        String auth_token = null;

        //get current page URL
        String currentPage = request.getRequestURI();

        String[] excludedPages = {"/home", "/login", "/register", "/css/**", "/js/**"};

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("auth_token".equals(cookie.getName())) {
                    auth_token = cookie.getValue();
                    break;
                }
            }
        }
        
        if (auth_token != null && isValid(auth_token)) { 
            String username = userUtils.searchByToken(dbUtils.getConnection(), auth_token, "username");
            request.setAttribute("username", username);
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

    private boolean isValid(String token) {
        return userUtils.validateTokenID(dbUtils.getConnection(), token);
    }
}
