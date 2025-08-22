package com.voc.controller;

import java.util.regex.Pattern;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.voc.security.AuthManager;;

@Controller
public class RegisterController {

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
        @RequestParam String display_name, 
        @RequestParam String username, 
        @RequestParam String password,
        @RequestParam String confirmPassword, 
        Model model) {

        username = username.trim();
        display_name = display_name.trim();
        password = password.trim();
        confirmPassword = confirmPassword.trim();

        if (username == null || username.isEmpty() 
            || password == null || password.isEmpty()
            || confirmPassword == null || confirmPassword.isEmpty()) {
            model.addAttribute("error", "Username and/or password cannot be empty.");
            return "register";
        }

        if (!password.equals(confirmPassword)){
            model.addAttribute("error", "Your password is not matched.");
            return "register";
        }

        if(!Pattern.compile("^(?!.*_.*_)[a-z_]{3,20}$").matcher(username).find()) {
            model.addAttribute("error", "Username must be 3-20 characters long, contain only lowercase letters and underscores, and cannot contain consecutive underscores.");
            return "register";
        }
        // password conditions
        if(!Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@/$%^&*-]).{8,}$").matcher(password).find()) {
            model.addAttribute("error", "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
            return "register";
        }

        if (AuthManager.registerUser(display_name, username, password)) {
            System.out.println("Registeration successful");
            return "redirect:/login";
        } else {
            model.addAttribute("error", "Username already exists.");
            return "register"; // return to registration page with an error message
        }
    }
}
