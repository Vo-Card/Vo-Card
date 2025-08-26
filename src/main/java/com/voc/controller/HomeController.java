package com.voc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String homePage(Model model) {
        return "index";
    }

    @RequestMapping("/helloworld")
    public String helloworld() {
        return "helloworld";
    }

    @RequestMapping("/u/welcome")
    public String welcome() {
        return "/u/welcome";
    }

    @RequestMapping("/u/your-deck")
    public String yourDeck() {
        return "/u/your-deck";
    }

    @RequestMapping("/not_found")
    public String NotfoundPage() {
        return "not_found";
    }
}
