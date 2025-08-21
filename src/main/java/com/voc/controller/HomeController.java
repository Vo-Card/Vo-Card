package com.voc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.voc.handler.Formatter;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String homePage(Model model) {
        Formatter formatter = new Formatter();
        return "index";
    }

    @RequestMapping("/helloworld")
    public String helloworld() {
        return "helloworld";
    }

    @RequestMapping("/welcome")
    public String welcome() {
        return "welcome";
    }
}