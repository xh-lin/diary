package com.xuhuang.diary.controllers.view.v1;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AppController {

    private static final String REDIRECT_DIARY = "redirect:/diary";

    @GetMapping
    public String viewHomePage(Model model) {
        return REDIRECT_DIARY;
    }

}
