package com.xuhuang.diary.controllers.api.v1;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.xuhuang.diary.utils.JsonUtil.asJsonString;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository mockUserRepository;

    @Test
    void registerValidateUsername() throws Exception {
        // only -, _, letters or numbers
        RegisterRequest requestBody = new RegisterRequest("test1:)", "test1@test.com", "Qwerty123.", "Qwerty123.");

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors", hasItem(RegisterRequest.VALIDATION_MESSAGE_USERNAME_CONTAIN_ONLY)));

        verify(mockUserRepository, times(0)).save(any(User.class));

        // at least one letter or number
        requestBody = new RegisterRequest("-__-", "test1@test.com", "Qwerty123.", "Qwerty123.");

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors", hasItem(RegisterRequest.VALIDATION_MESSAGE_USERNAME_AT_LEAST)));

        verify(mockUserRepository, times(0)).save(any(User.class));

        // size
        requestBody = new RegisterRequest("1", "test1@test.com", "Qwerty123.", "Qwerty123.");

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors", hasItem(
                RegisterRequest.VALIDATION_MESSAGE_USERNAME_SIZE
                    .replace("{min}", String.valueOf(RegisterRequest.USERNAME_SIZE_MIN))
                    .replace("{max}", String.valueOf(RegisterRequest.USERNAME_SIZE_MAX)))));

        verify(mockUserRepository, times(0)).save(any(User.class));

        // not blank
        requestBody = new RegisterRequest(null, "test1@test.com", "Qwerty123.", "Qwerty123.");

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors", hasItem(RegisterRequest.VALIDATION_MESSAGE_USERNAME_NOTBLANK)));

        verify(mockUserRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerValidateEmail() throws Exception {
        // email format
        RegisterRequest requestBody = new RegisterRequest("test1", "test1", "Qwerty123.", "Qwerty123.");

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors", hasItem(RegisterRequest.VALIDATION_MESSAGE_EMAIL)));

        verify(mockUserRepository, times(0)).save(any(User.class));

        // not blank
        requestBody = new RegisterRequest("test1", null, "Qwerty123.", "Qwerty123.");

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors", hasItem(RegisterRequest.VALIDATION_MESSAGE_EMAIL_NOTBLANK)));

        verify(mockUserRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerValidatePassword() throws Exception {
        // at least one lowercase letter
        RegisterRequest requestBody = new RegisterRequest("test1", "test1@test.com", "QWERTY123.", "QWERTY123.");

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors", hasItem(RegisterRequest.VALIDATION_MESSAGE_PASSWORD_LOWER)));

        verify(mockUserRepository, times(0)).save(any(User.class));

        // at least one uppercase letter
        requestBody = new RegisterRequest("test1", "test1@test.com", "qwerty123.", "qwerty123.");

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors", hasItem(RegisterRequest.VALIDATION_MESSAGE_PASSWORD_UPPER)));

        verify(mockUserRepository, times(0)).save(any(User.class));

        // at least one number
        requestBody = new RegisterRequest("test1", "test1@test.com", "Qwertyabc.", "Qwertyabc.");

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors", hasItem(RegisterRequest.VALIDATION_MESSAGE_PASSWORD_NUMBER)));

        verify(mockUserRepository, times(0)).save(any(User.class));

        // at least one special character
        requestBody = new RegisterRequest("test1", "test1@test.com", "Qwerty123", "Qwerty123");

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors", hasItem(RegisterRequest.VALIDATION_MESSAGE_PASSWORD_SPECIAL)));

        verify(mockUserRepository, times(0)).save(any(User.class));

        // size
        requestBody = new RegisterRequest("test1", "test1@test.com", "Qw1.", "Qw1.");

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors", hasItem(
                RegisterRequest.VALIDATION_MESSAGE_PASSWORD_SIZE
                    .replace("{min}", String.valueOf(RegisterRequest.PASSWORD_SIZE_MIN))
                    .replace("{max}", String.valueOf(RegisterRequest.PASSWORD_SIZE_MAX)))));

        verify(mockUserRepository, times(0)).save(any(User.class));

        // not blank
        requestBody = new RegisterRequest("test1", "test1@test.com", null, null);

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(2)))
            .andExpect(jsonPath("$.errors", hasItem(RegisterRequest.VALIDATION_MESSAGE_PASSWORD_NOTBLANK)))
            .andExpect(jsonPath("$.errors", hasItem(RegisterRequest.VALIDATION_MESSAGE_PASSWORD_CONFIRM_NOTBLANK)));

        verify(mockUserRepository, times(0)).save(any(User.class));

        // password confirmation
        requestBody = new RegisterRequest("test1", "test1@test.com", "Qwerty123.", "Qwerty123");

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors", hasItem(RegisterRequest.VALIDATION_MESSAGE_PASSWORD_CONFIRMATION)));

        verify(mockUserRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerConflict() throws Exception {
        Optional<User> mockUser = Optional.ofNullable(new User("test1", "test1@test.com", null, UserRole.USER));

        doReturn(mockUser).when(mockUserRepository).findByUsername("test1");
        doReturn(mockUser).when(mockUserRepository).findByEmail("test1@test.com");

        // username and email taken
        RegisterRequest requestBody = new RegisterRequest("test1", "test1@test.com", "Qwerty123.", "Qwerty123.");

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
            .andDo(print())
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(2)))
            .andExpect(jsonPath("$.errors", hasItem(UserService.USERNAME_ALREADY_TAKEN)))
            .andExpect(jsonPath("$.errors", hasItem(UserService.EMAIL_ALREADY_TAKEN)));

        verify(mockUserRepository, times(0)).save(any(User.class));

        // username taken
        requestBody = new RegisterRequest("test1", "test2@test.com", "Qwerty123.", "Qwerty123.");

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
            .andDo(print())
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors", hasItem(UserService.USERNAME_ALREADY_TAKEN)));

        verify(mockUserRepository, times(0)).save(any(User.class));

        // email taken
        requestBody = new RegisterRequest("test2", "test1@test.com", "Qwerty123.", "Qwerty123.");

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
            .andDo(print())
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors", hasItem(UserService.EMAIL_ALREADY_TAKEN)));

        verify(mockUserRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerSuccess() throws Exception {
        RegisterRequest requestBody = new RegisterRequest("test1", "test1@test.com", "Qwerty123.", "Qwerty123.");

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
            .andDo(print())
            .andExpect(status().isCreated());

        verify(mockUserRepository, times(1)).save(any(User.class));
    }

    @Test
    void loginValidations() throws Exception {
        LoginRequest requestBody = new LoginRequest(null, null);

        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors", hasSize(2)))
            .andExpect(jsonPath("$.errors", hasItem(LoginRequest.VALIDATION_MESSAGE_USERNAME_NOTBLANK)))
            .andExpect(jsonPath("$.errors", hasItem(LoginRequest.VALIDATION_MESSAGE_PASSWORD_NOTBLANK)));
    }

}
