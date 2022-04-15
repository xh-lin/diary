package com.xuhuang.diary.controllers.exceptionhandlers;

import java.util.NoSuchElementException;

import javax.security.auth.message.AuthException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice("com.xuhuang.diary.controllers.view")
public class ViewExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex) {
        HttpStatus status;

        if (ex instanceof AuthException) {
            status = HttpStatus.FORBIDDEN;
        } else if (ex instanceof NoSuchElementException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        log.info("ex: {}, status: {}", ex, status);
        return String.format("redirect:/error/%d?message=%s", status.value(), ex.getMessage());
    }

}
