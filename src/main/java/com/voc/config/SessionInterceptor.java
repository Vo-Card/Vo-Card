package com.voc.config;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import com.voc.security.AuthManager;
import com.voc.utils.Row;

/**
 * SessionInterceptor ensures that HTTP requests have a valid user session.
 * <p>
 * Requests without a valid session will be redirected to the login page,
 * except for a defined list of excluded pages (public pages, static resources,
 * etc.).
 * </p>
 * 
 * <p>
 * This interceptor automatically injects the username into the request
 * attributes
 * when a valid session is found.
 * </p>
 */
@Configuration
public class SessionInterceptor implements HandlerInterceptor {

    /**
     * Pre-handle method invoked before controller methods.
     * Checks for a valid session cookie and redirects to login if session is
     * missing or invalid.
     *
     * @param request  The HTTP request
     * @param response The HTTP response
     * @param handler  The handler object
     * @return true if request should proceed, false if redirected
     * @throws Exception in case of unexpected errors
     */
    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        String sessionToken = null;

        // Get the current page URL
        String currentPage = request.getRequestURI();

        // Pages that don't require authentication
        String[] excludedPages = { 
            "/home", "/login",
            "/register", "/css/**",
            "/js/**", "/resources/**",
<<<<<<< HEAD
            "/api/auth/**", "/about", "/contact"
=======
            "/api/auth/**", "/about", "/contact",
            "/404"
>>>>>>> b5e346b73ed8227b1349ed778c9c3dfffcf0a06a
         };

        // Look for session cookie
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("session_id".equals(cookie.getName())) {
                    sessionToken = cookie.getValue();
                    break;
                }
            }
        }

        // Validate session token if present
        if (sessionToken != null) {
            Row userData = AuthManager.validateUserSession(sessionToken);
            if (userData != null) {
                request.setAttribute("username", (String) userData.get("username"));
                return true;
            }
        }

        // Allow access to excluded/public pages
        for (String page : excludedPages) {
            if (currentPage.matches(page.replace("**", ".*"))) {
                return true;
            }
        }

        // Redirect to login if session is missing/invalid
        response.sendRedirect("/login");
        return false;
    }
}
