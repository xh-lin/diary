package com.xuhuang.diary.controllers.exceptionhandlers;

import java.util.NoSuchElementException;

import javax.security.auth.message.AuthException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice("com.xuhuang.diary.controllers.view")
public class ViewExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex) {
        if (ex instanceof IllegalArgumentException) {
            return "redirect:/error/400";
        } else if (ex instanceof AuthException) {
            return "redirect:/error/403";
        } else if (ex instanceof NoSuchElementException) {
            return "redirect:/error/404";
        } else {
            return "redirect:/error/500";
        }
    }

}
