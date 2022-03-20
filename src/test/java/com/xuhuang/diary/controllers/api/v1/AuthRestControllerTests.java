package com.xuhuang.diary.controllers.api.v1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import com.xuhuang.diary.domains.LoginRequest;
import com.xuhuang.diary.domains.RegisterRequest;
import com.xuhuang.diary.models.User;
import com.xuhuang.diary.models.UserRole;
import com.xuhuang.diary.repositories.UserRepository;
import com.xuhuang.diary.services.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
@AutoConfigureMockMvc
class AuthRestControllerTests extends RestControllerTests {

    private static final String API_V1_AUTH_LOGIN = "/api/v1/auth/login";
    private static final String API_V1_AUTH_REGISTER = "/api/v1/auth/register";

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private UserRepository mockUserRepository;

    @Test
    void registerValidateUsername() throws Exception {
        // only -, _, letters or numbers
        RegisterRequest requestBody = new RegisterRequest("test1:)", "test1@test.com", "Qwerty123.", "Qwerty123.");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_USERNAME_CONTAIN_ONLY);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // at least one letter or number
        requestBody = new RegisterRequest("-__-", "test1@test.com", "Qwerty123.", "Qwerty123.");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_USERNAME_AT_LEAST);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // size
        requestBody = new RegisterRequest("1", "test1@test.com", "Qwerty123.", "Qwerty123.");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_USERNAME_SIZE
                .replace("{min}", String.valueOf(RegisterRequest.USERNAME_SIZE_MIN))
                .replace("{max}", String.valueOf(RegisterRequest.USERNAME_SIZE_MAX)));

        verify(mockUserRepository, times(0)).save(any(User.class));

        // not blank
        requestBody = new RegisterRequest(null, "test1@test.com", "Qwerty123.", "Qwerty123.");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_USERNAME_NOTBLANK);

        verify(mockUserRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerValidateEmail() throws Exception {
        // email format
        RegisterRequest requestBody = new RegisterRequest("test1", "test1", "Qwerty123.", "Qwerty123.");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_EMAIL);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // not blank
        requestBody = new RegisterRequest("test1", null, "Qwerty123.", "Qwerty123.");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_EMAIL_NOTBLANK);

        verify(mockUserRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerValidatePassword() throws Exception {
        // at least one lowercase letter
        RegisterRequest requestBody = new RegisterRequest("test1", "test1@test.com", "QWERTY123.", "QWERTY123.");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_PASSWORD_LOWER);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // at least one uppercase letter
        requestBody = new RegisterRequest("test1", "test1@test.com", "qwerty123.", "qwerty123.");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_PASSWORD_UPPER);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // at least one number
        requestBody = new RegisterRequest("test1", "test1@test.com", "Qwertyabc.", "Qwertyabc.");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_PASSWORD_NUMBER);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // at least one special character
        requestBody = new RegisterRequest("test1", "test1@test.com", "Qwerty123", "Qwerty123");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_PASSWORD_SPECIAL);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // size
        requestBody = new RegisterRequest("test1", "test1@test.com", "Qw1.", "Qw1.");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_PASSWORD_SIZE
                .replace("{min}", String.valueOf(RegisterRequest.PASSWORD_SIZE_MIN))
                .replace("{max}", String.valueOf(RegisterRequest.PASSWORD_SIZE_MAX)));

        verify(mockUserRepository, times(0)).save(any(User.class));

        // not blank
        requestBody = new RegisterRequest("test1", "test1@test.com", null, null);

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_PASSWORD_NOTBLANK,
            RegisterRequest.VALIDATION_MESSAGE_PASSWORD_CONFIRM_NOTBLANK);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // password confirmation
        requestBody = new RegisterRequest("test1", "test1@test.com", "Qwerty123.", "Qwerty123");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_PASSWORD_CONFIRMATION);

        verify(mockUserRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerConflict() throws Exception {
        setUpMockUser();

        // username and email taken
        RegisterRequest requestBody = new RegisterRequest("test1", "test1@test.com", "Qwerty123.", "Qwerty123.");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.CONFLICT, true,
            UserService.USERNAME_ALREADY_TAKEN,
            UserService.EMAIL_ALREADY_TAKEN);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // username taken
        requestBody = new RegisterRequest("test1", "test2@test.com", "Qwerty123.", "Qwerty123.");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.CONFLICT, true,
            UserService.USERNAME_ALREADY_TAKEN);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // email taken
        requestBody = new RegisterRequest("test2", "test1@test.com", "Qwerty123.", "Qwerty123.");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.CONFLICT, true,
            UserService.EMAIL_ALREADY_TAKEN);

        verify(mockUserRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerSuccess() throws Exception {
        RegisterRequest requestBody = new RegisterRequest("test1", "test1@test.com", "Qwerty123.", "Qwerty123.");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.CREATED);

        verify(mockUserRepository, times(1)).save(any(User.class));
    }

    @Test
    void loginValidations() throws Exception {
        LoginRequest requestBody = new LoginRequest(null, null);

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_LOGIN, requestBody,
            HttpStatus.BAD_REQUEST, true,
            LoginRequest.VALIDATION_MESSAGE_USERNAME_NOTBLANK,
            LoginRequest.VALIDATION_MESSAGE_PASSWORD_NOTBLANK);
    }

    @Test
    void loginSuccess() throws Exception {
        setUpMockUser();

        LoginRequest requestBody = new LoginRequest("test1", "Qwerty123.");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_LOGIN, requestBody,
            HttpStatus.OK);
    }

    @Test
    void loginFailure() throws Exception {
        setUpMockUser();

        LoginRequest requestBody = new LoginRequest("test1", "123");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_LOGIN, requestBody,
            HttpStatus.UNAUTHORIZED);
    }

    private void setUpMockUser() {
        String encryptedPassword = bCryptPasswordEncoder.encode("Qwerty123.");
        Optional<User> mockUser = Optional.ofNullable(
            new User("test1", "test1@test.com", encryptedPassword, UserRole.USER));

        doReturn(mockUser).when(mockUserRepository).findByUsername("test1");
        doReturn(mockUser).when(mockUserRepository).findByEmail("test1@test.com");
    }

}
