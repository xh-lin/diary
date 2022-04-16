package com.xuhuang.diary.controllers.exceptionhandlers;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.security.auth.message.AuthException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;

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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice("com.xuhuang.diary.controllers.api")
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String MESSAGES = "messages";
    private static final String MESSAGE = "message";

    // error handle for RequestBody validation
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

        body.put(TIMESTAMP, new Date());
        body.put(STATUS, status.value());
        body.put(ERROR, status.getReasonPhrase());
        body.put(MESSAGES, messages);

        ResponseEntity<Object> response = new ResponseEntity<>(body, headers, status);
        log.info("{}", response);
        return response;
    }

    // error handle for PathVariable and RequestParam validation
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        Map<Object, Object> messages = new LinkedHashMap<>();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        for (ConstraintViolation<?> cv : ex.getConstraintViolations()) {
            String varName = "";
            //  get the name of last node
            for (Path.Node node : cv.getPropertyPath())  {
                varName = node.getName();
            }
            messages.put(varName, cv.getMessage());
        }

        body.put(TIMESTAMP, new Date());
        body.put(STATUS, status.value());
        body.put(ERROR, status.getReasonPhrase());
        body.put(MESSAGES, messages);

        ResponseEntity<Object> response = new ResponseEntity<>(body, status);
        log.info("{}", response);
        return response;
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

        body.put(TIMESTAMP, new Date());
        body.put(STATUS, status.value());
        body.put(ERROR, status.getReasonPhrase());
        body.put(MESSAGE, ex.getMessage());

        ResponseEntity<Object> response = new ResponseEntity<>(body, status);
        log.info("{}", response);
        return response;
    }

}
