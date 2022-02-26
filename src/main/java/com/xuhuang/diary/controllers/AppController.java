package com.xuhuang.diary.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.security.auth.message.AuthException;
import javax.validation.Valid;

import com.xuhuang.diary.domains.RegisterRequest;
import com.xuhuang.diary.services.UserService;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
        return Template.INDEX.toString();
    }
    
    @GetMapping("/register")
    public String viewRegistrationPage(Model model) {
        model.addAttribute("request", new RegisterRequest());
        return Template.REGISTER.toString();
    }

    @PostMapping("/register")
    public String submitRegistration(@Valid RegisterRequest request, BindingResult br, Model model) {
        if (br.hasErrors()) {
            List<String> errors = br.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

            model.addAttribute("request", request);
            model.addAttribute("errors", errors);
            return Template.REGISTER.toString();
        }

        try {
            userService.register(request);
        } catch (AuthException e) {
            List<String> errors = new ArrayList<>();
            errors.add(e.getMessage());

            model.addAttribute("request", request);
            model.addAttribute("errors", errors);
            return Template.REGISTER.toString();
        }
        
        return Template.LOGIN.toString();
    }

    @GetMapping("/login")
    public String viewLoginPage() {
        return Template.LOGIN.toString();
    }

}
