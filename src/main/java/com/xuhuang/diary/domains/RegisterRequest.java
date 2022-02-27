package com.xuhuang.diary.domains;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.xuhuang.diary.validators.FieldMatch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldMatch(fields = {"password", "passwordConfirm"}, message = "Password confirmation does not match.")
public class RegisterRequest {

    public static final String VALIDATION_MESSAGE_USERNAME_CONTAIN_ONLY = "Username must contain only hyphens (-), underscores (_), letters or numbers.";
    public static final String VALIDATION_MESSAGE_USERNAME_AT_LEAST = "Username must contain at least one letter or number.";
    public static final String VALIDATION_MESSAGE_USERNAME_SIZE = "Username length must be between {min} and {max}.";
    public static final String VALIDATION_MESSAGE_EMAIL = "Email format not valid.";
    public static final int USERNAME_SIZE_MIN = 4;
    public static final int USERNAME_SIZE_MAX = 16;
    
    public static final String VALIDATION_MESSAGE_PASSWORD_LOWER = "Password must contain at least one lowercase letter.";
    public static final String VALIDATION_MESSAGE_PASSWORD_UPPER = "Password must contain at least one uppercase letter.";
    public static final String VALIDATION_MESSAGE_PASSWORD_NUMBER = "Password must contain at least one number.";
    public static final String VALIDATION_MESSAGE_PASSWORD_SPECIAL = "Password must contain at least one special character.";
    public static final String VALIDATION_MESSAGE_PASSWORD_SIZE = "Password length must be between {min} and {max}.";
    public static final int PASSWORD_SIZE_MIN = 8;
    public static final int PASSWORD_SIZE_MAX = 32;

    @Pattern(
        regexp = "^[a-zA-Z0-9_-]*$", 
        message = VALIDATION_MESSAGE_USERNAME_CONTAIN_ONLY)
    @Pattern(
        regexp = "^.*[a-zA-Z0-9].*$", 
        message = VALIDATION_MESSAGE_USERNAME_AT_LEAST)
    @Size(
        min = USERNAME_SIZE_MIN, max = USERNAME_SIZE_MAX,
        message = VALIDATION_MESSAGE_USERNAME_SIZE)
    private String username;

    @Email(message = VALIDATION_MESSAGE_EMAIL)
    private String email;

    @Pattern(
        regexp = "^.*[a-z].*$", 
        message = VALIDATION_MESSAGE_PASSWORD_LOWER)
    @Pattern(
        regexp = "^.*[A-Z].*$", 
        message = VALIDATION_MESSAGE_PASSWORD_UPPER)
    @Pattern(
        regexp = "^.*[0-9].*$", 
        message = VALIDATION_MESSAGE_PASSWORD_NUMBER)
    @Pattern(
        regexp = "^.*[^\\w\\s].*$", 
        message = VALIDATION_MESSAGE_PASSWORD_SPECIAL)
    @Size(
        min = PASSWORD_SIZE_MIN, max = PASSWORD_SIZE_MAX,
        message = VALIDATION_MESSAGE_PASSWORD_SIZE)
    private String password;

    private String passwordConfirm;

}
