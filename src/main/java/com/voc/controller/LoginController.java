package com.voc.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.voc.database.DatabaseUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
