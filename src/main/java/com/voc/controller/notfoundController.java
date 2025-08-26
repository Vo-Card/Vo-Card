package com.voc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class notfoundControllers {

    @GetMapping("/not_found")
    public String dashboard() {
        return "not_found";
    }
}
}
