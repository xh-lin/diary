package com.xuhuang.diary.controllers;

import com.xuhuang.diary.domains.RegistrationRequest;
import com.xuhuang.diary.services.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        model.addAttribute("username", userDetails.getUsername());
        return "index";
    }
    
    @GetMapping("/register")
    public String viewRegistrationPage(Model model) {
        model.addAttribute("request", new RegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String submitRegistration(RegistrationRequest request, Model model) {
        userService.register(request);
        return "redirect:/";
    }
}
