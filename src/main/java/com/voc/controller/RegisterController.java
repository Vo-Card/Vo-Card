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
    public String registerUser(@RequestParam String username, @RequestParam String password,
            @RequestParam String confirmPassword) {
        System.out.println("registering user");
        String result = processRegistration(username, password, confirmPassword);

        if ("registrationSuccess".equals(result)) {
            return "redirect:/login";
        } else {
            return "register"; // return to registration page with an error message
        }
    }

    // Example method to process registration
    private String processRegistration(String username, String password, String confirmPassword) {
        if (!userUtils.userExists(dbUtils.getConnection(), username) && password == confirmPassword) { // Replace null
                                                                                                       // with actual
                                                                                                       // database
                                                                                                       // connection
            System.out.println("Process Regis");
            userUtils.createUser(dbUtils.getConnection(), username, password);
            return "registrationSuccess";
        } else {
            return "registrationFailed";
        }
    }
}
