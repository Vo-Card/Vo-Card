package com.voc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.voc.handler.Formatter;
import com.voc.handler.deckConfig;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String homePage(Model model) {
        Formatter formatter = new Formatter();
        formatter.randomizeWordsFromeDeck(null);
        return "index";
    }

    @RequestMapping("/helloworld")
    public String helloworld() {
        return "helloworld";
    }
}