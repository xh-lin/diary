package com.xuhuang.diary.controllers.view.v1;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.xuhuang.diary.controllers.view.Template;
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
public class AuthController {

    private static final String REDIRECT_ROOT = "redirect:/";
    private static final String REQUEST = "request";
    private static final String ERRORS = "errors";

    private final UserService userService;

    @GetMapping("/register")
    public String viewRegistration(Model model) {
        if (userService.getCurrentUser() != null) {
            // redirect to home page if logged in already
            return REDIRECT_ROOT;
        }
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

        userService.register(request);
        return Template.LOGIN.toString();
    }

    @GetMapping("/login")
    public String viewLogin() {
        if (userService.getCurrentUser() != null) {
            // redirect to home page if logged in already
            return REDIRECT_ROOT;
        }
        return Template.LOGIN.toString();
    }

}
