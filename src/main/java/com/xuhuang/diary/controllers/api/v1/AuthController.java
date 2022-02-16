package com.xuhuang.diary.controllers.api.v1;

import com.xuhuang.diary.domains.AuthRequest;
import com.xuhuang.diary.services.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        try {
            userService.register(request);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("registered successfully", HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        try {
            SecurityContextHolder.getContext().setAuthentication(
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())));
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Logged-in failed.", HttpStatus.OK);
        }

        return new ResponseEntity<>("Logged-in successfully", HttpStatus.OK);
    }
    
}
