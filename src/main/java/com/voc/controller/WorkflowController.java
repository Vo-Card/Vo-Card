package com.voc.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/workflow")
public class WorkflowController {

    @GetMapping("/{page}")
    public String getWorkflowPage(
            @PathVariable("page") String page,
            HttpServletRequest request) {

        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        if (isAjax) {
            return "workflow/" + page;
        } else {
            request.setAttribute("page", page);
            return "workflow";
        }
    }

    @GetMapping
    public String workflowDefault() {
        return "workflow/home"; 
    }
}
