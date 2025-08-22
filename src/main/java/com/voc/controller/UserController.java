package com.voc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    
    @GetMapping("/u/dashboard")
    public String dashboard() {
        return "/u/dashboard";
    }
}
