package com.voc.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.voc.database.DatabaseUtils;
import com.voc.database.Security;
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

    @PostMapping("/login")
    public String processLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) {
        // Validate username and password
        if (username == null || username.isEmpty() ||
            password == null || password.isEmpty()) {
            model.addAttribute("error", "Username and/or password cannot be empty.");
            return "login";
        }

        String session_id = Security.loginUser(username, password, request.getHeader("X-Forwarded-For"), request.getHeader("User-Agent"));
        
        if(session_id == null){
            model.addAttribute("error", "Invalid username or password.");
            return "login";
        }

        Cookie cookie = new Cookie("session_id", session_id);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return "redirect:/";
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
