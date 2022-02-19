package com.xuhuang.diary.controllers;

import com.xuhuang.diary.domains.AuthRequest;
import com.xuhuang.diary.services.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AppController {

    private final UserService userService;
    
    @GetMapping("")
    public String viewHomePage(Model model) {
        model.addAttribute("username", userService.getCurrentUser().getUsername());
        return "index";
    }
    
    @GetMapping("/register")
    public String viewRegistrationPage(Model model) {
        model.addAttribute("request", new AuthRequest());
        return "register";
    }

    @PostMapping("/register")
    public String submitRegistration(AuthRequest request, Model model) {
        try {
            userService.register(request);
        } catch (Exception e) {
            model.addAttribute("request", request);
            model.addAttribute("errorMsg", e.getMessage());
            return "register";
        }
        
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String viewLoginPage() {
        return "login";
    }
}
