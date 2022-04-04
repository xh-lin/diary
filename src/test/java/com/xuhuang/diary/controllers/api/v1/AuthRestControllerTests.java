package com.xuhuang.diary.controllers.api.v1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import com.xuhuang.diary.domains.LoginRequest;
import com.xuhuang.diary.domains.RegisterRequest;
import com.xuhuang.diary.models.User;
import com.xuhuang.diary.repositories.UserRepository;
import com.xuhuang.diary.services.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@SpringBootTest
@AutoConfigureMockMvc
class AuthRestControllerTests extends RestControllerTests {

    private static final String API_V1_AUTH_LOGIN = "/api/v1/auth/login";
    private static final String API_V1_AUTH_REGISTER = "/api/v1/auth/register";

    @MockBean
    private UserRepository mockUserRepository;

    @Test
    void registerValidateUsername() throws Exception {
        // only -, _, letters or numbers
        RegisterRequest requestBody = new RegisterRequest("user:)", MOCK_EMAIL, MOCK_PASSWORD, MOCK_PASSWORD);

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_USERNAME_CONTAIN_ONLY);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // at least one letter or number
        requestBody.setUsername("-__-");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_USERNAME_AT_LEAST);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // size
        requestBody.setUsername("1");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_USERNAME_SIZE
                .replace("{min}", String.valueOf(RegisterRequest.USERNAME_SIZE_MIN))
                .replace("{max}", String.valueOf(RegisterRequest.USERNAME_SIZE_MAX)));

        verify(mockUserRepository, times(0)).save(any(User.class));

        // not blank
        requestBody.setUsername(null);

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_USERNAME_NOTBLANK);

        verify(mockUserRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerValidateEmail() throws Exception {
        // email format
        RegisterRequest requestBody = new RegisterRequest(MOCK_USERNAME, "badEmail", MOCK_PASSWORD, MOCK_PASSWORD);

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_EMAIL);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // not blank
        requestBody.setEmail(null);

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_EMAIL_NOTBLANK);

        verify(mockUserRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerValidatePassword() throws Exception {
        // at least one lowercase letter
        String password = "QWERTY123.";
        RegisterRequest requestBody = new RegisterRequest(MOCK_USERNAME, MOCK_EMAIL, password, password);

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_PASSWORD_LOWER);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // at least one uppercase letter
        password = "qwerty123.";
        requestBody.setPassword(password);
        requestBody.setPasswordConfirm(password);

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_PASSWORD_UPPER);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // at least one number
        password = "Qwertyabc.";
        requestBody.setPassword(password);
        requestBody.setPasswordConfirm(password);

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_PASSWORD_NUMBER);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // at least one special character
        password = "Qwerty123";
        requestBody.setPassword(password);
        requestBody.setPasswordConfirm(password);

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_PASSWORD_SPECIAL);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // size
        password = "Qw1.";
        requestBody.setPassword(password);
        requestBody.setPasswordConfirm(password);

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_PASSWORD_SIZE
                .replace("{min}", String.valueOf(RegisterRequest.PASSWORD_SIZE_MIN))
                .replace("{max}", String.valueOf(RegisterRequest.PASSWORD_SIZE_MAX)));

        verify(mockUserRepository, times(0)).save(any(User.class));

        // not blank
        password = null;
        requestBody.setPassword(password);
        requestBody.setPasswordConfirm(password);

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_PASSWORD_NOTBLANK,
            RegisterRequest.VALIDATION_MESSAGE_PASSWORD_CONFIRM_NOTBLANK);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // password confirmation
        requestBody.setPassword(MOCK_PASSWORD);
        requestBody.setPasswordConfirm("differentPassword");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.BAD_REQUEST, true,
            RegisterRequest.VALIDATION_MESSAGE_PASSWORD_CONFIRMATION);

        verify(mockUserRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerConflict() throws Exception {
        setupMockUserRepository();

        // username and email taken
        RegisterRequest requestBody = new RegisterRequest(MOCK_USERNAME, MOCK_EMAIL, MOCK_PASSWORD, MOCK_PASSWORD);

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.CONFLICT, true,
            UserService.USERNAME_ALREADY_TAKEN,
            UserService.EMAIL_ALREADY_TAKEN);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // username taken
        requestBody = new RegisterRequest(MOCK_USERNAME, "another@user.com", MOCK_PASSWORD, MOCK_PASSWORD);

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.CONFLICT, true,
            UserService.USERNAME_ALREADY_TAKEN);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // email taken
        requestBody = new RegisterRequest("anotherUser", MOCK_EMAIL, MOCK_PASSWORD, MOCK_PASSWORD);

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.CONFLICT, true,
            UserService.EMAIL_ALREADY_TAKEN);

        verify(mockUserRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerSuccess() throws Exception {
        RegisterRequest requestBody = new RegisterRequest(MOCK_USERNAME, MOCK_EMAIL, MOCK_PASSWORD, MOCK_PASSWORD);

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_REGISTER, requestBody,
            HttpStatus.CREATED);

        verify(mockUserRepository, times(1)).save(any(User.class));
    }

    @Test
    void loginValidations() throws Exception {
        // email and password not blank
        LoginRequest requestBody = new LoginRequest(null, null);

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_LOGIN, requestBody,
            HttpStatus.BAD_REQUEST, true,
            LoginRequest.VALIDATION_MESSAGE_USERNAME_NOTBLANK,
            LoginRequest.VALIDATION_MESSAGE_PASSWORD_NOTBLANK);
    }

    @Test
    void loginSuccess() throws Exception {
        setupMockUserRepository();

        LoginRequest requestBody = new LoginRequest(MOCK_USERNAME, MOCK_PASSWORD);

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_LOGIN, requestBody,
            HttpStatus.OK);
    }

    @Test
    void loginFailure() throws Exception {
        setupMockUserRepository();

        LoginRequest requestBody = new LoginRequest(MOCK_USERNAME, "wrongPassword");

        mockMvcTest(
            HttpMethod.POST, API_V1_AUTH_LOGIN, requestBody,
            HttpStatus.UNAUTHORIZED);
    }

    private void setupMockUserRepository() {
        Optional<User> optionalMockUser = Optional.ofNullable(mockUser);
        doReturn(optionalMockUser).when(mockUserRepository).findByUsername(MOCK_USERNAME);
        doReturn(optionalMockUser).when(mockUserRepository).findByEmail(MOCK_EMAIL);
    }

}
