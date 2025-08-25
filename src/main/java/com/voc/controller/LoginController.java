package com.voc.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.voc.database.DatabaseUtils;
import com.voc.security.AuthManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        DatabaseUtils.checkDatabase();
        System.out.println("Database checked successfully.");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("session_id", ""); // match login cookie name
        cookie.setPath("/");
        cookie.setMaxAge(0); // expire immediately
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return "redirect:/home";
    }
}
