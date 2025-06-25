package com.voc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class RegisterController {

    @GetMapping("/register")
    public String showRegistrationPage() {
        return "register"; // return the name of the registration view
    }
    
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password) {
        String result = processRegistration(username, password);
        
        if ("registrationSuccess".equals(result)) {
            return "redirect:/login"; // redirect to login page after successful registration
        } else {
            return "register"; // return to registration page with an error message
        }
    }

    // Example method to process registration
    private String processRegistration(String username, String password) {
        return "registrationSuccess";
    }
}
