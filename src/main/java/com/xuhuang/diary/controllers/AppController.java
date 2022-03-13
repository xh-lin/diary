package com.xuhuang.diary.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AppController {

    @GetMapping
    public String viewHomePage(Model model) {
        return "redirect:/diary";
    }

}
