package com.voc.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import com.voc.security.SessionManager;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        return "register";
    }

    @GetMapping("/logout")
    public String logout(@CookieValue(name="session_token", required=false) String cookieValue,
                                    HttpServletResponse response) {
        if (cookieValue != null) {
            String[] parts = cookieValue.split(":");
            if (parts.length == 2) {
                String sessionId = parts[0];
                SessionManager.deleteSession(sessionId);
            }
        }

        // Invalidate the cookie anyway
        ResponseCookie emptyCookie = ResponseCookie.from("session_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", emptyCookie.toString());

        return "redirect:/login";
    }
}
