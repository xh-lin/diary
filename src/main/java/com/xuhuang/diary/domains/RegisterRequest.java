package com.xuhuang.diary.domains;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @Pattern(
        regexp = "^[a-zA-Z0-9_-]*$", 
        message = "Username must contain only hyphens (-), underscores (_), letters or numbers.")
    @Pattern(
        regexp = "^.*[a-zA-Z0-9].*$", 
        message = "Username must contain at least one letter or number.")
    @Size(
        min = 4, max = 16,
        message = "Username length must be between {min} and {max}.")
    private String username;

    @Email(message = "Email format not valid.")
    private String email;

    @Pattern(
        regexp = "^.*[a-z].*$", 
        message = "Password must contain at least one lowercase letter.")
    @Pattern(
        regexp = "^.*[A-Z].*$", 
        message = "Password must contain at least one uppercase letter.")
    @Pattern(
        regexp = "^.*[0-9].*$", 
        message = "Password must contain at least one number.")
    @Pattern(
        regexp = "^.*[^\\w\\s].*$", 
        message = "Password must contain at least one special character.")
    @Size(
        min = 8, max = 32,
        message = "Password length must be between {min} and {max}.")
    private String password;

    private String passwordConfirm;

}
