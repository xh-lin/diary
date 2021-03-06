package com.xuhuang.diary.controllers.api.v1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
class TagRestControllerTests extends BaseRestControllerTests {

    private static final String API_V1_TAG = "/api/v1/tag";
    private static final String API_V1_TAG_TAGID = "/api/v1/tag/{tagId}";

    private static final String MESSAGE_JPEXP = "$.message";

    private static final String NAME = "name";

    private static final Long MOCK_TAG_ID = 1L;
    private static final String MOCK_TAG_NAME = "Mock Tag";
    private static final Long NOT_FOUND_TAG_ID = 2L;

    private static User mockUser;
    private static User anotherMockUser;

    @MockBean
    private TagRepository mockTagRepository;

    @BeforeAll
    static void setup() {
        mockUser = mockUser(1L);
        anotherMockUser = mockUser(2L);
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
        // bad request not blank
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add(NAME, "");

        mockMvcPerform(
                HttpMethod.POST, API_V1_TAG,
                requestParams, mockUser,
                HttpStatus.BAD_REQUEST)
                .andExpect(jsonPath("$.messages.name").value(TagRestController.NAME_NOTBLANK));

        verify(mockTagRepository, times(0)).save(any(Tag.class));

        // bad request size
        String s = "0123456789";
        requestParams.clear();
        requestParams.add(NAME, s.repeat(Tag.NAME_LENGTH / 10 + 1));

        mockMvcPerform(
                HttpMethod.POST, API_V1_TAG,
                requestParams, mockUser,
                HttpStatus.BAD_REQUEST)
                .andExpect(jsonPath("$.messages.name").value(
                        TagRestController.NAME_SIZE.replace("{max}", String.valueOf(Tag.NAME_LENGTH))));

        verify(mockTagRepository, times(0)).save(any(Tag.class));
    }

    @Test
    void getTagsSuccess() throws Exception {
        setupMockRepository();

        mockMvcPerform(
                HttpMethod.GET, API_V1_TAG,
                mockUser,
                HttpStatus.OK)
                .andExpect(jsonPath(MESSAGE_JPEXP).value(TagRestController.FETCHED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data[0].name").value(MOCK_TAG_NAME));
    }

    @Test
    void getTagSuccess() throws Exception {
        setupMockRepository();

        Object[] uriVars = { MOCK_TAG_ID };

        mockMvcPerform(
                HttpMethod.GET, API_V1_TAG_TAGID,
                uriVars, mockUser,
                HttpStatus.OK)
                .andExpect(jsonPath(MESSAGE_JPEXP).value(TagRestController.FETCHED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data.name").value(MOCK_TAG_NAME));
    }

    @Test
    void getTagFailure() throws Exception {
        setupMockRepository();

        // forbidden
        Object[] uriVars = { MOCK_TAG_ID };

        mockMvcPerform(
                HttpMethod.GET, API_V1_TAG_TAGID,
                uriVars, anotherMockUser,
                HttpStatus.FORBIDDEN)
                .andExpect(jsonPath(MESSAGE_JPEXP).value(TagService.YOU_DO_NOT_HAVE_PERMISSION_TO_ACCESS));

        // not found
        uriVars[0] = NOT_FOUND_TAG_ID;

        mockMvcPerform(
                HttpMethod.GET, API_V1_TAG_TAGID,
                uriVars, mockUser,
                HttpStatus.NOT_FOUND);
    }

    @Test
    void updateTagSuccess() throws Exception {
        setupMockRepository();

        Object[] uriVars = { MOCK_TAG_ID };
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add(NAME, "My New Tag");

        mockMvcPerform(
                HttpMethod.PUT, API_V1_TAG_TAGID,
                uriVars, requestParams, mockUser,
                HttpStatus.OK);

        verify(mockTagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void updateTagFailure() throws Exception {
        setupMockRepository();

        // forbidden
        Object[] uriVars = { MOCK_TAG_ID };
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add(NAME, "My New Tag");

        mockMvcPerform(
                HttpMethod.PUT, API_V1_TAG_TAGID,
                uriVars, requestParams, anotherMockUser,
                HttpStatus.FORBIDDEN);

        verify(mockTagRepository, times(0)).save(any(Tag.class));

        // not found
        uriVars[0] = NOT_FOUND_TAG_ID;

        mockMvcPerform(
                HttpMethod.PUT, API_V1_TAG_TAGID,
                uriVars, requestParams, mockUser,
                HttpStatus.NOT_FOUND);

        verify(mockTagRepository, times(0)).save(any(Tag.class));

        // bad request not blank
        uriVars[0] = MOCK_TAG_ID;
        requestParams.clear();
        requestParams.add(NAME, "");

        mockMvcPerform(
                HttpMethod.PUT, API_V1_TAG_TAGID,
                uriVars, requestParams, mockUser,
                HttpStatus.BAD_REQUEST)
                .andExpect(jsonPath("$.messages.name").value(TagRestController.NAME_NOTBLANK));

        verify(mockTagRepository, times(0)).save(any(Tag.class));

        // bad request size
        String s = "0123456789";
        requestParams.clear();
        requestParams.add(NAME, s.repeat(Tag.NAME_LENGTH / 10 + 1));

        mockMvcPerform(
                HttpMethod.PUT, API_V1_TAG_TAGID,
                uriVars, requestParams, mockUser,
                HttpStatus.BAD_REQUEST)
                .andExpect(jsonPath("$.messages.name").value(
                        TagRestController.NAME_SIZE.replace("{max}", String.valueOf(Tag.NAME_LENGTH))));

        verify(mockTagRepository, times(0)).save(any(Tag.class));
    }

    @Test
    void deleteTagSuccess() throws Exception {
        setupMockRepository();

        Object[] uriVars = { MOCK_TAG_ID };

        mockMvcPerform(
                HttpMethod.DELETE, API_V1_TAG_TAGID,
                uriVars, mockUser,
                HttpStatus.OK);

        verify(mockTagRepository, times(1)).deleteById(any(Long.class));
    }

    @Test
    void deleteTagFailure() throws Exception {
        setupMockRepository();

        // forbidden
        Object[] uriVars = { MOCK_TAG_ID };

        mockMvcPerform(
                HttpMethod.DELETE, API_V1_TAG_TAGID,
                uriVars, anotherMockUser,
                HttpStatus.FORBIDDEN);

        verify(mockTagRepository, times(0)).deleteById(any(Long.class));

        // not found
        uriVars[0] = NOT_FOUND_TAG_ID;

        mockMvcPerform(
                HttpMethod.DELETE, API_V1_TAG_TAGID,
                uriVars, mockUser,
                HttpStatus.NOT_FOUND);

        verify(mockTagRepository, times(0)).deleteById(any(Long.class));
    }

    private void setupMockRepository() {
        // tag of mockUser
        Tag mockTag = new Tag(MOCK_TAG_NAME, mockUser);
        mockTag.setId(MOCK_TAG_ID);
        mockUser.getTags().add(mockTag);

        // setup mock repository for tag
        Optional<Tag> optionalMockTag = Optional.ofNullable(mockTag);
        List<Tag> mockTags = Arrays.asList(mockTag);
        doReturn(optionalMockTag).when(mockTagRepository).findById(MOCK_TAG_ID);
        doReturn(mockTags).when(mockTagRepository).findByUser(mockUser);
    }

}
