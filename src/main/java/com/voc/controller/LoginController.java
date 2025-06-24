package com.voc.controller;

import javax.servlet.http.HttpSession;
import com.voc.database.valicateDatabase;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        valicateDatabase db = new valicateDatabase();
        db.checkDatabase(); // Ensure the database is checked before showing the login page
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
