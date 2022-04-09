package com.xuhuang.diary.controllers.view.v1;

import com.xuhuang.diary.controllers.view.Template;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/error")
@RequiredArgsConstructor
public class ErrorController {

    @GetMapping("/{statusCode}")
    public String viewError(Model model, @PathVariable int statusCode, @RequestParam(required = false) String message) {
        HttpStatus status = HttpStatus.valueOf(statusCode);
        model.addAttribute("status", status.value());
        model.addAttribute("error", status.getReasonPhrase());
        model.addAttribute("message", message);
        return Template.ERROR.toString();
    }

}
