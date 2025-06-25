package com.voc.controller;

import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import com.voc.database.dbUtils;
import com.voc.database.userUtils;
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
        
            // username conditions ^(?!.*_.*_)[a-z_]{1,20}$
        if(!Pattern.compile("^(?!.*_.*_)[a-z_]{3,20}$")
               .matcher(username)
               .find()) {
            model.addAttribute("error", "Username must be 3-20 characters long, contain only lowercase letters and underscores, and cannot contain consecutive underscores.");
            return "login";
        }
        // password conditions
        if(Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")
               .matcher(password)
               .find()) {
            model.addAttribute("error", "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
            return "login";
        }
        // Give the client a session id as a cookie
        return "login";

    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // clear session
        return "redirect:/login";
    }
}
