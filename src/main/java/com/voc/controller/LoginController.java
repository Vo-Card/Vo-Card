package com.voc.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.voc.database.dbUtils;
import com.voc.database.userUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        System.out.println("Database checked successfully.");
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response,
            Model model) {
        System.out.println("Processing login for user: " + username);
        // Validate username and password
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            model.addAttribute("error", "Username and/or password cannot be empty.");
            return "login"; // return to login page with an error message
        }

        if (userUtils.validateUser(dbUtils.getConnection(), username, password)) {
            // Generate token
            String token = userUtils.getUserTokenID(dbUtils.getConnection(), username);
            Cookie authCookie = new Cookie("auth_token", token);

            authCookie.setHttpOnly(true);
            authCookie.setPath("/");

            response.addCookie(authCookie);

            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid username or password.");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("auth_token", "");
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire immediately
        response.addCookie(cookie);
        return "redirect:/login";
    }
}
