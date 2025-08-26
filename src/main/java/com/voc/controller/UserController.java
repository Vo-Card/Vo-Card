package com.voc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/workflow")
public class UserController {

    @RequestMapping("home")
    public String dashboard() {
        return "/workflow/welcome";
    }

    @RequestMapping("decks")
    public String yourDeck() {
        return "/workflow/decks";
    }

}
