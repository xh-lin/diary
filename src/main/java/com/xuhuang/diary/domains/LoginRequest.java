package com.xuhuang.diary.domains;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginRequest {

    public static final String USERNAME_SIZE = "Username length must be between {min} and {max}.";
    public static final String USERNAME_NOTBLANK = "Username must not be blank.";
    public static final int USERNAME_SIZE_MIN = 4;
    public static final int USERNAME_SIZE_MAX = 63;

    public static final String PASSWORD_SIZE = "Password length must be between {min} and {max}.";
    public static final String PASSWORD_NOTBLANK = "Password must not be blank.";
    public static final int PASSWORD_SIZE_MIN = 8;
    public static final int PASSWORD_SIZE_MAX = 127;

    @Size(min = USERNAME_SIZE_MIN, max = USERNAME_SIZE_MAX, message = USERNAME_SIZE)
    @NotBlank(message = USERNAME_NOTBLANK)
    private String username;

    @Size(min = PASSWORD_SIZE_MIN, max = PASSWORD_SIZE_MAX, message = PASSWORD_SIZE)
    @NotBlank(message = PASSWORD_NOTBLANK)
    @JsonIgnore
    @ToString.Exclude
    private String password;

}
