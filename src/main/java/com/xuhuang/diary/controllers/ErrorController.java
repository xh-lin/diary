package com.xuhuang.diary.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("error")
@RequiredArgsConstructor
public class ErrorController {

    private static final String TITLE_400 = "400 Bad Request";
    private static final String MESSAGE_400 = "Bad Request";
    private static final String TITLE_403 = "403 Forbidden";
    private static final String MESSAGE_403 = "Forbidden";
    private static final String TITLE_404 = "404 Not Found";
    private static final String MESSAGE_404 = "Not Found";
    private static final String TITLE_500 = "500 Internal Server Error";
    private static final String MESSAGE_500 = "Internal Server Error";

    private static final String TITLE = "title";
    private static final String CODE = "code";
    private static final String MESSAGE = "message";

    @GetMapping("/400")
    public String view400(Model model) {
        return viewError(model, TITLE_400, "400", MESSAGE_400);
    }

    @GetMapping("/403")
    public String view403(Model model) {
        return viewError(model, TITLE_403, "403", MESSAGE_403);
    }

    @GetMapping("/404")
    public String view404(Model model) {
        return viewError(model, TITLE_404, "404", MESSAGE_404);
    }

    @GetMapping("/500")
    public String view500(Model model) {
        return viewError(model, TITLE_500, "500", MESSAGE_500);
    }

    private String viewError(Model model, String title, String code, String message) {
        model.addAttribute(TITLE, title);
        model.addAttribute(CODE, code);
        model.addAttribute(MESSAGE, message);
        return Template.ERROR.toString();
    }

}
