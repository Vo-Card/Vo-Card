package com.voc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/index")
    public String helloPage(Model model) {
        model.addAttribute("username", "fsdgsdfdsgdfg");
        return "index";
    }
}