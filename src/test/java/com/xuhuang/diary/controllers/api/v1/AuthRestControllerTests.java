package com.xuhuang.diary.controllers.api.v1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Optional;

import com.xuhuang.diary.domains.LoginRequest;
import com.xuhuang.diary.domains.RegisterRequest;
import com.xuhuang.diary.models.User;
import com.xuhuang.diary.repositories.UserRepository;

import org.junit.jupiter.api.BeforeAll;
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

    private static User mockUser;

    @MockBean
    private UserRepository mockUserRepository;

    @BeforeAll
    static void setup() {
        mockUser = mockUser(1L);
    }

    @Test
    void registerValidateUsername() throws Exception {
        // only -, _, letters or numbers
        RegisterRequest requestBody = new RegisterRequest(
                "user:)",
                mockUser.getEmail(),
                MOCK_PASSWORD,
                MOCK_PASSWORD);

        expectArray(
                mockMvcPerform(
                        HttpMethod.POST, API_V1_AUTH_REGISTER,
                        requestBody,
                        HttpStatus.BAD_REQUEST),
                ERRORS_JPEXP,
                RegisterRequest.USERNAME_CONTAIN_ONLY);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // at least one letter or number
        requestBody.setUsername("-__-");

        expectArray(
                mockMvcPerform(
                        HttpMethod.POST, API_V1_AUTH_REGISTER,
                        requestBody,
                        HttpStatus.BAD_REQUEST),
                ERRORS_JPEXP,
                RegisterRequest.USERNAME_AT_LEAST);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // size
        requestBody.setUsername("1");

        expectArray(
                mockMvcPerform(
                        HttpMethod.POST, API_V1_AUTH_REGISTER,
                        requestBody,
                        HttpStatus.BAD_REQUEST),
                ERRORS_JPEXP,
                RegisterRequest.USERNAME_SIZE
                        .replace("{min}", String.valueOf(RegisterRequest.USERNAME_SIZE_MIN))
                        .replace("{max}", String.valueOf(RegisterRequest.USERNAME_SIZE_MAX)));

        verify(mockUserRepository, times(0)).save(any(User.class));

        // not blank
        requestBody.setUsername(null);

        expectArray(
                mockMvcPerform(
                        HttpMethod.POST, API_V1_AUTH_REGISTER,
                        requestBody,
                        HttpStatus.BAD_REQUEST),
                ERRORS_JPEXP,
                RegisterRequest.USERNAME_NOTBLANK);

        verify(mockUserRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerValidateEmail() throws Exception {
        // email format
        RegisterRequest requestBody = new RegisterRequest(
                mockUser.getUsername(),
                "badEmail",
                MOCK_PASSWORD,
                MOCK_PASSWORD);

        expectArray(
                mockMvcPerform(
                        HttpMethod.POST, API_V1_AUTH_REGISTER,
                        requestBody,
                        HttpStatus.BAD_REQUEST),
                ERRORS_JPEXP,
                RegisterRequest.EMAIL);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // not blank
        requestBody.setEmail(null);

        expectArray(
                mockMvcPerform(
                        HttpMethod.POST, API_V1_AUTH_REGISTER,
                        requestBody,
                        HttpStatus.BAD_REQUEST),
                ERRORS_JPEXP,
                RegisterRequest.EMAIL_NOTBLANK);

        verify(mockUserRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerValidatePassword() throws Exception {
        // at least one lowercase letter
        String password = "QWERTY123.";
        RegisterRequest requestBody = new RegisterRequest(
                mockUser.getUsername(),
                mockUser.getEmail(),
                password,
                password);

        expectArray(
                mockMvcPerform(
                        HttpMethod.POST, API_V1_AUTH_REGISTER,
                        requestBody,
                        HttpStatus.BAD_REQUEST),
                ERRORS_JPEXP,
                RegisterRequest.PASSWORD_LOWER);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // at least one uppercase letter
        password = "qwerty123.";
        requestBody.setPassword(password);
        requestBody.setPasswordConfirm(password);

        expectArray(
                mockMvcPerform(
                        HttpMethod.POST, API_V1_AUTH_REGISTER,
                        requestBody,
                        HttpStatus.BAD_REQUEST),
                ERRORS_JPEXP,
                RegisterRequest.PASSWORD_UPPER);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // at least one number
        password = "Qwertyabc.";
        requestBody.setPassword(password);
        requestBody.setPasswordConfirm(password);

        expectArray(
                mockMvcPerform(
                        HttpMethod.POST, API_V1_AUTH_REGISTER,
                        requestBody,
                        HttpStatus.BAD_REQUEST),
                ERRORS_JPEXP,
                RegisterRequest.PASSWORD_NUMBER);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // at least one special character
        password = "Qwerty123";
        requestBody.setPassword(password);
        requestBody.setPasswordConfirm(password);

        expectArray(
                mockMvcPerform(
                        HttpMethod.POST, API_V1_AUTH_REGISTER,
                        requestBody,
                        HttpStatus.BAD_REQUEST),
                ERRORS_JPEXP,
                RegisterRequest.PASSWORD_SPECIAL);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // size
        password = "Qw1.";
        requestBody.setPassword(password);
        requestBody.setPasswordConfirm(password);

        expectArray(
                mockMvcPerform(
                        HttpMethod.POST, API_V1_AUTH_REGISTER,
                        requestBody,
                        HttpStatus.BAD_REQUEST),
                ERRORS_JPEXP,
                RegisterRequest.PASSWORD_SIZE
                        .replace("{min}", String.valueOf(RegisterRequest.PASSWORD_SIZE_MIN))
                        .replace("{max}", String.valueOf(RegisterRequest.PASSWORD_SIZE_MAX)));

        verify(mockUserRepository, times(0)).save(any(User.class));

        // not blank
        password = null;
        requestBody.setPassword(password);
        requestBody.setPasswordConfirm(password);

        expectArray(
                mockMvcPerform(
                        HttpMethod.POST, API_V1_AUTH_REGISTER,
                        requestBody,
                        HttpStatus.BAD_REQUEST),
                ERRORS_JPEXP,
                RegisterRequest.PASSWORD_NOTBLANK,
                RegisterRequest.PASSWORD_CONFIRM_NOTBLANK);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // password confirmation
        requestBody.setPassword(MOCK_PASSWORD);
        requestBody.setPasswordConfirm("differentPassword");

        expectArray(
                mockMvcPerform(
                        HttpMethod.POST, API_V1_AUTH_REGISTER,
                        requestBody,
                        HttpStatus.BAD_REQUEST),
                ERRORS_JPEXP,
                RegisterRequest.PASSWORD_CONFIRMATION);

        verify(mockUserRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerConflict() throws Exception {
        setupMockRepository();

        // username and email taken
        RegisterRequest requestBody = new RegisterRequest(
                mockUser.getUsername(),
                mockUser.getEmail(),
                MOCK_PASSWORD,
                MOCK_PASSWORD);

        expectArray(
                mockMvcPerform(
                        HttpMethod.POST, API_V1_AUTH_REGISTER,
                        requestBody,
                        HttpStatus.BAD_REQUEST),
                ERRORS_JPEXP,
                RegisterRequest.USERNAME_ALREADY_TAKEN,
                RegisterRequest.EMAIL_ALREADY_TAKEN);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // username taken
        requestBody.setEmail("another@user.com");

        expectArray(
                mockMvcPerform(
                        HttpMethod.POST, API_V1_AUTH_REGISTER,
                        requestBody,
                        HttpStatus.BAD_REQUEST),
                ERRORS_JPEXP,
                RegisterRequest.USERNAME_ALREADY_TAKEN);

        verify(mockUserRepository, times(0)).save(any(User.class));

        // email taken
        requestBody.setEmail(mockUser.getEmail());
        requestBody.setUsername("anotherUser");

        expectArray(
                mockMvcPerform(
                        HttpMethod.POST, API_V1_AUTH_REGISTER,
                        requestBody,
                        HttpStatus.BAD_REQUEST),
                ERRORS_JPEXP,
                RegisterRequest.EMAIL_ALREADY_TAKEN);

        verify(mockUserRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerSuccess() throws Exception {
        RegisterRequest requestBody = new RegisterRequest(
                mockUser.getUsername(),
                mockUser.getEmail(),
                MOCK_PASSWORD,
                MOCK_PASSWORD);

        mockMvcPerform(
                HttpMethod.POST, API_V1_AUTH_REGISTER,
                requestBody,
                HttpStatus.CREATED)
                .andExpect(jsonPath(MESSAGE_JPEXP).value(AuthRestController.REGISTERED_SUCCESSFULLY));

        verify(mockUserRepository, times(1)).save(any(User.class));
    }

    @Test
    void loginValidations() throws Exception {
        // email and password not blank
        LoginRequest requestBody = new LoginRequest(null, null);

        expectArray(
                mockMvcPerform(
                        HttpMethod.POST, API_V1_AUTH_LOGIN,
                        requestBody,
                        HttpStatus.BAD_REQUEST),
                ERRORS_JPEXP,
                LoginRequest.USERNAME_NOTBLANK,
                LoginRequest.PASSWORD_NOTBLANK);
    }

    @Test
    void loginSuccess() throws Exception {
        setupMockRepository();

        LoginRequest requestBody = new LoginRequest(mockUser.getUsername(), MOCK_PASSWORD);

        mockMvcPerform(
                HttpMethod.POST, API_V1_AUTH_LOGIN,
                requestBody,
                HttpStatus.OK)
                .andExpect(jsonPath(MESSAGE_JPEXP).value(AuthRestController.LOGGED_IN_SUCCESSFULLY));
    }

    @Test
    void loginFailure() throws Exception {
        setupMockRepository();

        LoginRequest requestBody = new LoginRequest(mockUser.getUsername(), "wrongPassword");

        mockMvcPerform(
                HttpMethod.POST, API_V1_AUTH_LOGIN,
                requestBody,
                HttpStatus.UNAUTHORIZED)
                .andExpect(jsonPath(ERROR_JPEXP).value(AuthRestController.INVALID_USERNAME_AND_PASSWORD));
    }

    private void setupMockRepository() {
        Optional<User> optionalMockUser = Optional.ofNullable(mockUser);
        doReturn(optionalMockUser).when(mockUserRepository).findByUsername(mockUser.getUsername());
        doReturn(optionalMockUser).when(mockUserRepository).findByEmail(mockUser.getEmail());
    }

}
