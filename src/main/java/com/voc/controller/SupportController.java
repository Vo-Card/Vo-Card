package com.voc.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/support")
public class SupportController {
    @GetMapping("/{page}")

    public String getStaticPage(@PathVariable("page") String page,
            HttpServletRequest request,
            HttpServletResponse response) {
        String jspPath = "/WEB-INF/jsp/support/" + page + ".jsp";
        File file = new File(request.getServletContext().getRealPath(jspPath));
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        } else {
            return "support/" + page;
        }
    }
}
