package com.xuhuang.diary.domains;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.xuhuang.diary.validators.FieldMatch;
import com.xuhuang.diary.validators.UniqueEmail;
import com.xuhuang.diary.validators.UniqueUsername;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldMatch(fields = { "password", "passwordConfirm" }, message = RegisterRequest.PASSWORD_CONFIRMATION)
public class RegisterRequest {

    public static final String USERNAME_ALREADY_TAKEN = "Username already taken.";
    public static final String USERNAME_CONTAIN_ONLY = "Username must contain only hyphens (-), underscores (_), letters or numbers.";
    public static final String USERNAME_AT_LEAST = "Username must contain at least one letter or number.";
    public static final String USERNAME_SIZE = "Username length must be between {min} and {max}.";
    public static final String USERNAME_NOTBLANK = "Username must not be blank.";
    public static final int USERNAME_SIZE_MIN = 4;
    public static final int USERNAME_SIZE_MAX = 16;

    public static final String EMAIL_ALREADY_TAKEN = "Email already taken.";
    public static final String EMAIL = "Email format not valid.";
    public static final String EMAIL_NOTBLANK = "Email must not be blank.";

    public static final String PASSWORD_LOWER = "Password must contain at least one lowercase letter.";
    public static final String PASSWORD_UPPER = "Password must contain at least one uppercase letter.";
    public static final String PASSWORD_NUMBER = "Password must contain at least one number.";
    public static final String PASSWORD_SPECIAL = "Password must contain at least one special character.";
    public static final String PASSWORD_SIZE = "Password length must be between {min} and {max}.";
    public static final String PASSWORD_NOTBLANK = "Password must not be blank.";
    public static final int PASSWORD_SIZE_MIN = 8;
    public static final int PASSWORD_SIZE_MAX = 32;

    public static final String PASSWORD_CONFIRM_NOTBLANK = "Password confirmation must not be blank.";
    public static final String PASSWORD_CONFIRMATION = "Password confirmation does not match.";

    @UniqueUsername(message = USERNAME_ALREADY_TAKEN)
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$", message = USERNAME_CONTAIN_ONLY)
    @Pattern(regexp = "^.*[a-zA-Z0-9].*$", message = USERNAME_AT_LEAST)
    @Size(min = USERNAME_SIZE_MIN, max = USERNAME_SIZE_MAX, message = USERNAME_SIZE)
    @NotBlank(message = USERNAME_NOTBLANK)
    private String username;

    @UniqueEmail(message = EMAIL_ALREADY_TAKEN)
    @Email(message = EMAIL)
    @NotBlank(message = EMAIL_NOTBLANK)
    private String email;

    @Pattern(regexp = "^.*[a-z].*$", message = PASSWORD_LOWER)
    @Pattern(regexp = "^.*[A-Z].*$", message = PASSWORD_UPPER)
    @Pattern(regexp = "^.*[0-9].*$", message = PASSWORD_NUMBER)
    @Pattern(regexp = "^.*[^\\w\\s].*$", message = PASSWORD_SPECIAL)
    @Size(min = PASSWORD_SIZE_MIN, max = PASSWORD_SIZE_MAX, message = PASSWORD_SIZE)
    @NotBlank(message = PASSWORD_NOTBLANK)
    private String password;

    @NotBlank(message = PASSWORD_CONFIRM_NOTBLANK)
    private String passwordConfirm;

}
