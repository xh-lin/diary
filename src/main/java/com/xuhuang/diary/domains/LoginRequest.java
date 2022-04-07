package com.xuhuang.diary.domains;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    public static final String VALIDATION_MESSAGE_USERNAME_NOTBLANK = "Username must not be blank.";
    public static final String VALIDATION_MESSAGE_PASSWORD_NOTBLANK = "Password must not be blank.";

    @NotBlank(message = VALIDATION_MESSAGE_USERNAME_NOTBLANK)
    private String username;

    @NotBlank(message = VALIDATION_MESSAGE_PASSWORD_NOTBLANK)
    private String password;

}
