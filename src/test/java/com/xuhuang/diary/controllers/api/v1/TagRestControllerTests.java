package com.xuhuang.diary.controllers.api.v1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.xuhuang.diary.models.Tag;
import com.xuhuang.diary.models.User;
import com.xuhuang.diary.repositories.TagRepository;
import com.xuhuang.diary.services.TagService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest
@AutoConfigureMockMvc
public class TagRestControllerTests extends BaseRestControllerTests {

    private static final String API_V1_TAG = "/api/v1/tag";

    private static final String MESSAGE_JPEXP = "$.message";

    private static final String NAME = "name";

    private static User mockUser;

    @MockBean
    private TagRepository mockTagRepository;

    @BeforeAll
    static void setup() {
        mockUser = mockUser(1L);
    }

    @Test
    void createTagSuccess() throws Exception {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add(NAME, "My Diary");

        mockMvcPerform(
                HttpMethod.POST, API_V1_TAG,
                requestParams, mockUser,
                HttpStatus.CREATED)
                .andExpect(jsonPath(MESSAGE_JPEXP).value(TagRestController.CREATED_SUCCESSFULLY));

        verify(mockTagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void createTagFailure() throws Exception {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add(NAME, "");

        mockMvcPerform(
                HttpMethod.POST, API_V1_TAG,
                requestParams, mockUser,
                HttpStatus.BAD_REQUEST)
                .andExpect(jsonPath(MESSAGE_JPEXP).value(TagService.NAME_MUST_NOT_BE_BLANK));

        verify(mockTagRepository, times(0)).save(any(Tag.class));
    }

}
