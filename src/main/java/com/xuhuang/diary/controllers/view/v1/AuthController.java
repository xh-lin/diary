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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private static final String REDIRECT_ROOT = "redirect:/";
    private static final String REQUEST_BODY = "requestBody";
    private static final String ERROR_MESSAGES = "errorMessages";

    private final UserService userService;

    @GetMapping("/register")
    public String viewRegistration(Model model) {
        log.info("viewRegistration()");

        if (userService.getCurrentUser() != null) {
            // redirect to home page if logged in already
            return REDIRECT_ROOT;
        }

        model.addAttribute(REQUEST_BODY, new RegisterRequest());

        log.info("{}", model.asMap());
        return Template.REGISTER.toString();
    }

    @PostMapping("/register")
    public String submitRegistration(Model model, @Valid RegisterRequest requestBody, BindingResult br) {
        log.info("submitRegistration()");

        if (br.hasErrors()) {
            MultiValueMap<String, String> errorMessages = new LinkedMultiValueMap<>();

            for (FieldError error : br.getFieldErrors()) {
                errorMessages.add(error.getField(), error.getDefaultMessage());
            }

            List<String> global = br.getGlobalErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            if (!global.isEmpty()) {
                errorMessages.put("passwordConfirm", global);
            }

            model.addAttribute(ERROR_MESSAGES, errorMessages);
            model.addAttribute(REQUEST_BODY, requestBody);

            log.info("{}", model.asMap());
            return Template.REGISTER.toString();
        }

        userService.register(requestBody);
        return Template.LOGIN.toString();
    }

    @GetMapping("/login")
    public String viewLogin() {
        log.info("viewLogin()");

        if (userService.getCurrentUser() != null) {
            // redirect to home page if logged in already
            return REDIRECT_ROOT;
        }

        return Template.LOGIN.toString();
    }

}
