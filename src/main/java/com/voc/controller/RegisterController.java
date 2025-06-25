package com.voc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.voc.database.dbUtils;
import com.voc.database.userUtils;

public class RegisterController {

    @GetMapping("/register")
    public String showRegistrationPage() {
        return "register"; // return the name of the registration view
    }
    
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password) {
        String result = processRegistration(username, password);
        
        if ("registrationSuccess".equals(result)) {
            return "redirect:/login";
        } else {
            return "register"; // return to registration page with an error message
        }
    }

    // Example method to process registration
    private String processRegistration(String username, String password) {
        userUtils.createUser(dbUtils.getConnection(), username, password); // Replace null with actual database connection
        if (userUtils.userExists(null, username)) { // Replace null with actual database connection
            return "registrationSuccess"; // Registration successful
        } else {
            return "registrationFailed"; // Registration failed, user already exists
        }
    }
}
