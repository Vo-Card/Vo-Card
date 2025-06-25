package com.voc.controller;

import javax.servlet.http.HttpSession;
import com.voc.security.tokenUtils;

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
        //test token
        String token = tokenUtils.generateToken("Zartex", 1,"Zartex1234!");
        System.out.println("Generated token: " + token);
        System.out.println("Username from token: " + tokenUtils.getUsernameFromToken(token));
        System.out.println("User ID from token: " + tokenUtils.getUserIdFromToken(token));
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        
        // You can replace this with database/user-service check
        if ("admin".equals(username) && "1234".equals(password)) {
            session.setAttribute("username", username);
            return "redirect:/index"; // go to homepage after login
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // clear session
        return "redirect:/login";
    }
}
