package com.xuhuang.diary.controllers.api.v1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.xuhuang.diary.models.Book;
import com.xuhuang.diary.repositories.BookRepository;
import com.xuhuang.diary.repositories.RecordRepository;
import com.xuhuang.diary.repositories.UserRepository;
import com.xuhuang.diary.services.DiaryService;

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
public class DiaryRestControllerTests extends RestControllerTests {

    private static final String API_V1_DIARY = "/api/v1/diary";
    private static final String API_V1_DIARY_BOOKID = "/api/v1/diary/%d";
    private static final String API_V1_DIARY_BOOKID_RECORD = "/api/v1/diary/%d/record";
    private static final String API_V1_DIARY_BOOKID_RECORD_PAGE = "/api/v1/diary/%d/record/%d";
    private static final String API_V1_DIARY_BOOKID_RECORD_PAGE_SIZE = "/api/v1/diary/%d/record/%d/%d";
    private static final String API_V1_DIARY_RECORD_RECORDID = "/api/v1/diary/record/%d";

    @MockBean
    private UserRepository mockUserRepository;
    @MockBean
    private BookRepository mockBookRepository;
    @MockBean
    private RecordRepository mockRecordRepository;

    @Test
    void createBookSuccess() throws Exception {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("title", "My Diary");

        mockMvcTest(
            HttpMethod.POST, API_V1_DIARY,
            requestParams, mockUser,
            HttpStatus.CREATED);

        verify(mockBookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void createBookFailure() throws Exception {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("title", "");

        mockMvcTest(
            HttpMethod.POST, API_V1_DIARY,
            requestParams, mockUser,
            HttpStatus.BAD_REQUEST,
            DiaryService.TITLE_MUST_NOT_BE_BLANK);

        verify(mockBookRepository, times(0)).save(any(Book.class));
    }

}
