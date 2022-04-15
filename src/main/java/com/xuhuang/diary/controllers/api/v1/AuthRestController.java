package com.xuhuang.diary.controllers.api.v1;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.xuhuang.diary.domains.LoginRequest;
import com.xuhuang.diary.domains.RegisterRequest;
import com.xuhuang.diary.exceptions.LoginFailureException;
import com.xuhuang.diary.models.User;
import com.xuhuang.diary.services.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth") // Spring Security permit all /api/v*/auth/**
@RequiredArgsConstructor
public class AuthRestController {

    public static final String REGISTERED_SUCCESSFULLY = "Registered successfully.";
    public static final String INVALID_USERNAME_AND_PASSWORD = "Invalid username and password.";
    public static final String LOGGED_IN_SUCCESSFULLY = "Logged in successfully.";
    public static final String LOGGED_OUT_SUCCESSFULLY = "Logged out successfully.";

    private static final String MESSAGE = "message";

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request) {
        log.info("register(request: {})", request);

        Map<String, Object> body = new LinkedHashMap<>();
        userService.register(request);
        body.put(MESSAGE, REGISTERED_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.CREATED);
        log.info("{}", response);
        return response;
    }

    @PostMapping("login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request) throws LoginFailureException {
        log.info("login(request: {})", request.toString());

        Map<String, Object> body = new LinkedHashMap<>();

        try {
            SecurityContextHolder.getContext().setAuthentication(
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())));
        } catch (AuthenticationException e) {
            throw new LoginFailureException(INVALID_USERNAME_AND_PASSWORD);
        }

        body.put(MESSAGE, LOGGED_IN_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.OK);
        log.info("{}", response);
        return response;
    }

    @PostMapping("logout")
    public ResponseEntity<Object> logout(HttpServletRequest hsRequest, HttpServletResponse hsResponse) {
        User user = userService.getCurrentUser();
        log.info("logout(), user: {}", (user == null) ? null : user.getUsername());

        Map<String, Object> body = new LinkedHashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof AnonymousAuthenticationToken) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        new SecurityContextLogoutHandler().logout(hsRequest, hsResponse, auth);
        body.put(MESSAGE, LOGGED_OUT_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.OK);
        log.info("{}", response);
        return response;
    }

}
