package com.voc.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * WorkflowController handles requests for dynamic workflow pages.
 * <p>
 * It serves JSP pages located under /WEB-INF/jsp/workflow/ based on the URL path.
 * </p>
 * <p>
 * If a requested page does not exist, it returns a 404 error page.
 * </p>
 * <p>
 * The controller also supports AJAX requests by returning only the relevant
 * fragment of the page.
 * </p>
 */
@Controller
@RequestMapping("/workflow")
public class WorkflowController {

    /**
     * Handles requests for specific workflow pages.
     * <p>
     * If the page exists, it returns the corresponding JSP view.
     * If the page does not exist, it returns a 404 error page.
     * </p>
     * <p>
     * Supports both full page loads and AJAX requests.
     * </p>
     *
     * @param page     The requested page name
     * @param request  The HTTP request
     * @param response The HTTP response
     * @return The name of the JSP view to render
     */
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

    /**
     * Default redirect to /workflow/home
     * <p>
     * This handles requests to /workflow or /workflow/ and redirects to the home page.
     * </p>
     */
    @GetMapping({"", "/"})
    public String workflowDefaultRedirect() {
        return "redirect:/workflow/home";
    }
}
