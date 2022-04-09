package com.xuhuang.diary.controllers.exceptionhandlers;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.security.auth.message.AuthException;

import com.xuhuang.diary.exceptions.LoginFailureException;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice("com.xuhuang.diary.controllers.api")
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    // error handle for bean validations
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        // LinkedHashMap maintains insertion order
        Map<String, Object> body = new LinkedHashMap<>();
        MultiValueMap<String, String> messages = new LinkedMultiValueMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            messages.add(error.getField(), error.getDefaultMessage());
        }

        List<String> global = ex.getBindingResult().getGlobalErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        if (!global.isEmpty()) {
            messages.put("global", global);
        }

        body.put("timestamp", new Date());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("messages", messages);
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        HttpStatus status;

        if (ex instanceof LoginFailureException) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof AuthException) {
            status = HttpStatus.FORBIDDEN;
        } else if (ex instanceof NoSuchElementException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        body.put("timestamp", new Date());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, status);
    }

}
