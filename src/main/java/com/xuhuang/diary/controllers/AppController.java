package com.xuhuang.diary.controllers;

import com.xuhuang.diary.services.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AppController {

    private final UserService userService;

    @GetMapping("")
    public String viewHomePage(Model model) {
        model.addAttribute("username", userService.getCurrentUser().getUsername());
        return Template.INDEX.toString();
    }

}
