package com.voc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.voc.database.dbUtils;
import com.voc.database.userUtils;

@Controller
public class RegisterController {

    @GetMapping("/register")
    public String showRegistrationPage() {
        return "register";
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
        if (!userUtils.userExists(dbUtils.getConnection(), username)) { // Replace null with actual database connection
            userUtils.createUser(dbUtils.getConnection(), username, password);
            return "registrationSuccess";
        } else {
            return "registrationFailed";
        }
    }
}
