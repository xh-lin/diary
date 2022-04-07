package com.xuhuang.diary.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.xuhuang.diary.domains.RegisterRequest;
import com.xuhuang.diary.exceptions.RegisterException;
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
public class AuthController {

    private static final String REQUEST = "request";
    private static final String ERRORS = "errors";

    private final UserService userService;

    @GetMapping("/register")
    public String viewRegistration(Model model) {
        model.addAttribute(REQUEST, new RegisterRequest());
        return Template.REGISTER.toString();
    }

    @PostMapping("/register")
    public String submitRegistration(@Valid RegisterRequest request, BindingResult br, Model model) {
        if (br.hasErrors()) {
            List<String> errors = br.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

            model.addAttribute(REQUEST, request);
            model.addAttribute(ERRORS, errors);
            return Template.REGISTER.toString();
        }

        try {
            userService.register(request);
        } catch (RegisterException e) {
            List<String> errors = e.getMessages();

            model.addAttribute(REQUEST, request);
            model.addAttribute(ERRORS, errors);
            return Template.REGISTER.toString();
        }

        return Template.LOGIN.toString();
    }

    @GetMapping("/login")
    public String viewLogin() {
        return Template.LOGIN.toString();
    }

}
