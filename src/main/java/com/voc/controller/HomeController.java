package com.voc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.voc.database.DeckManager;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String homePage(Model model) {
        DeckManager.forceDefaultDeck();
        DeckManager.createCard(null);
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
}
