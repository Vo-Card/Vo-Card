package com.voc.controller;

import java.util.regex.Pattern;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.voc.database.dbUtils;
import com.voc.database.userUtils;

@Controller
public class RegisterController {

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password,
            @RequestParam String confirmPassword, Model model) {
        System.out.println("registering user...");
        // Validate username and password
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Confirm Password: " + confirmPassword);
        if (username == null || username.isEmpty() || password == null || password.isEmpty()
                || confirmPassword == null || confirmPassword.isEmpty()) {
            model.addAttribute("error", "Username and/or password cannot be empty.");
            return "register"; // return to registration page with an error message
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


        String result = processRegistration(username, password, confirmPassword);

        System.out.println("Registration result: " + result);

        if ("registrationSuccess".equals(result)) {
            return "redirect:/login";
        } else {
            return "register"; // return to registration page with an error message
        }
    }

    // Example method to process registration
    private String processRegistration(String username, String password, String confirmPassword) {
        if (!userUtils.userExists(dbUtils.getConnection(), username) && password.equals(confirmPassword)) { 
            System.out.println("Process Regis");
            userUtils.createUser(dbUtils.getConnection(), username, password);
            return "registrationSuccess";
        } else {
            return "registrationFailed";
        }
    }
}
