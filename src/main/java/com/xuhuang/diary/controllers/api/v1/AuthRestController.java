package com.xuhuang.diary.controllers.api.v1;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.xuhuang.diary.domains.LoginRequest;
import com.xuhuang.diary.domains.RegisterRequest;
import com.xuhuang.diary.services.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("api/v1/auth") // Spring Security permit all
@RequiredArgsConstructor
public class AuthRestController {

    public static final String REGISTERED_SUCCESSFULLY = "Registered successfully.";
    public static final String INVALID_USERNAME_AND_PASSWORD = "Invalid username and password.";
    public static final String LOGGED_IN_SUCCESSFULLY = "Logged in successfully.";
    public static final String LOGGED_OUT_SUCCESSFULLY = "Logged out successfully.";
    public static final String YOU_HAVE_NOT_LOGGED_IN = "You have not logged in";

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        
        try {
            userService.register(request);
        } catch (AuthException e) {
            body.put("error", e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.CONFLICT);
        }

        body.put("message", REGISTERED_SUCCESSFULLY);
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @PostMapping("login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        
        try {
            SecurityContextHolder.getContext().setAuthentication(
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())));
        } catch (AuthenticationException e) {
            body.put("error", INVALID_USERNAME_AND_PASSWORD);
            return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
        }

        body.put("message", LOGGED_IN_SUCCESSFULLY);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("logout")
    public ResponseEntity<Object> logout(HttpServletRequest request, HttpServletResponse response) {    
        Map<String, Object> body = new LinkedHashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            body.put("message", LOGGED_OUT_SUCCESSFULLY);
            return new ResponseEntity<>(body, HttpStatus.OK);
        }

        body.put("message", YOU_HAVE_NOT_LOGGED_IN);
        return new ResponseEntity<>(body, HttpStatus.NO_CONTENT);
    }
    
}
