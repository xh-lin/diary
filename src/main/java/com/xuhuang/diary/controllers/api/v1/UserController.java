package com.xuhuang.diary.controllers.api.v1;

import com.xuhuang.diary.domains.RegistrationRequest;
import com.xuhuang.diary.services.UserService;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("register")
    public String register(@RequestBody RegistrationRequest request) {
        // TODO: handle IllegalStateException
        return userService.register(request);
    }
    
}
