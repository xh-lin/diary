package com.xuhuang.diary.controllers.view.v1;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AppController {

    private static final String REDIRECT_DIARY = "redirect:/diary";

    @GetMapping
    public String viewHomePage(Model model) {
        log.info("viewHomePage()");
        return REDIRECT_DIARY;
    }

}
