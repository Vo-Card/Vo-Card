package com.voc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PublicController {

    @RequestMapping("/home")
    public String homePage() {
        return "index";
    }

    @RequestMapping("/helloworld")
    public String helloworld() {
        return "helloworld";
    }

    @RequestMapping("/about")
    public String aboutUs() {
        return "/about";
    }

    @RequestMapping("/contact")
    public String contactPage() {
        return "contact";
    }
}
