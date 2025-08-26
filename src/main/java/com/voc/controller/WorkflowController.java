package com.voc.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/workflow")
public class WorkflowController {

    @GetMapping("/{page}")
    public String getStaticPage(@PathVariable("page") String page, 
            HttpServletRequest request,
            HttpServletResponse response) {
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        String jspPath = "/WEB-INF/jsp/workflow/" + page + ".jsp";
        File file = new File(request.getServletContext().getRealPath(jspPath));
        if (!file.exists()) {
            if (isAjax) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            } else {
                return "404";
            }
        }

        if (isAjax) {
            return "workflow/" + page;
        } else {
            request.setAttribute("page", page);
            return "workflow";
        }
    }

    @GetMapping("/decks/{deckId}/{levelId}/{cardId}/edit")
    public String getDeckEditPage(
            @PathVariable String deckId,
            @PathVariable String levelId,
            @PathVariable String cardId,
            HttpServletRequest request) {

        request.setAttribute("deckId", deckId);
        request.setAttribute("levelId", levelId);
        request.setAttribute("cardId", cardId);
        request.setAttribute("page", "decks");

        return "workflow";
    }

    @GetMapping({"", "/"})
    public String workflowDefaultRedirect() {
        return "redirect:/workflow/home";
    }
}
