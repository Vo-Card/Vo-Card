package com.voc.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * workspaceController handles requests for dynamic workspace pages.
 * <p>
 * It serves JSP pages located under /WEB-INF/jsp/workspace/ based on the URL
 * path.
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
@RequestMapping("/workspace")
public class WorkspaceController {

    /**
     * Handles requests for specific workspace pages.
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
    public String getStaticPage(
            @PathVariable("page") String page,
            HttpServletRequest request,
            HttpServletResponse response) {

        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        String jspPath = "/WEB-INF/jsp/workspace/" + page + ".jsp";
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
            return "workspace/" + page;
        } else {
            return "workspace";
        }
    }

    @GetMapping({ "/decks/{deckId}", "/decks/{deckId}/{levelId}", "/decks/{deckId}/{levelId}/{cardId}" })
    public String getDeckSubPages(
            @PathVariable(required = false) Long deckId,
            @PathVariable(required = false) Long levelId,
            @PathVariable(required = false) Long cardId,
            HttpServletRequest request,
            HttpServletResponse response) {

        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        if (isAjax) {
            return "workspace/deckDetail";
        } else {
            return "workspace";
        }
    }

    @GetMapping("/root/assign")
    public String getAssignPage(
            HttpServletRequest request,
            HttpServletResponse response) {
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        if (isAjax) {
            return "workspace/root/assign";
        } else {
            return "workspace";
        }
    }

    @GetMapping("/root/{roleId}")
    public String getRootEditPage(
            @PathVariable(required = false) Long roleId,
            HttpServletRequest request,
            HttpServletResponse response) {
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        if (isAjax) {
            return "workspace/root/edit";
        } else {
            return "workspace";
        }
    }

    /**
     * Default redirect to /workspace/home
     * <p>
     * This handles requests to /workspace or /workspace/ and redirects to the home
     * page.
     * </p>
     */
    @GetMapping({ "", "/" })
    public String workspaceDefaultRedirect() {
        return "redirect:/workspace/home";
    }
}