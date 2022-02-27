package com.xuhuang.diary.controllers.api.v1;

import com.xuhuang.diary.domains.RegisterRequest;
import com.xuhuang.diary.models.User;
import com.xuhuang.diary.repositories.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.xuhuang.diary.utils.JsonUtil.asJsonString;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthRestControllerTests {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository mockUserRepository;
    
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

}
