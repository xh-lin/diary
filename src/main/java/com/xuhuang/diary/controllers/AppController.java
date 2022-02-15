package com.xuhuang.diary.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {
    
    @GetMapping("")
    public String viewHomePage() {
        return "index";
    }
    
}
