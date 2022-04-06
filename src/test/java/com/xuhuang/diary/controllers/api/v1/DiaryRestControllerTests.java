package com.xuhuang.diary.controllers.api.v1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.List;

import com.xuhuang.diary.models.Book;
import com.xuhuang.diary.models.User;
import com.xuhuang.diary.repositories.BookRepository;
import com.xuhuang.diary.repositories.RecordRepository;
import com.xuhuang.diary.repositories.UserRepository;
import com.xuhuang.diary.services.DiaryService;

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
public class DiaryRestControllerTests extends RestControllerTests {

    private static final String API_V1_DIARY = "/api/v1/diary";
    private static final String API_V1_DIARY_BOOKID = "/api/v1/diary/{bookId}";
    private static final String API_V1_DIARY_BOOKID_RECORD = "/api/v1/diary/{bookId}/record";
    private static final String API_V1_DIARY_BOOKID_RECORD_PAGE = "/api/v1/diary/{bookId}/record/{page}";
    private static final String API_V1_DIARY_BOOKID_RECORD_PAGE_SIZE = "/api/v1/diary/{bookId}/record/{page}/{size}";
    private static final String API_V1_DIARY_RECORD_RECORDID = "/api/v1/diary/record/{recordId}";

    private static final String MOCK_BOOK_TITLE = "Mock Diary";

    private static User mockUser;

    @MockBean
    private UserRepository mockUserRepository;
    @MockBean
    private BookRepository mockBookRepository;
    @MockBean
    private RecordRepository mockRecordRepository;

    @BeforeAll
    static void setup() {
        mockUser = mockUser();
    }

    @Test
    void createBookSuccess() throws Exception {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("title", "My Diary");

        mockMvcPerform(
            HttpMethod.POST, API_V1_DIARY,
            requestParams, mockUser,
            HttpStatus.CREATED)
            .andExpect(jsonPath(MESSAGE_JPEXP).value(DiaryRestController.CREATED_SUCCESSFULLY));

        verify(mockBookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void createBookFailure() throws Exception {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("title", "");

        mockMvcPerform(
            HttpMethod.POST, API_V1_DIARY,
            requestParams, mockUser,
            HttpStatus.BAD_REQUEST)
            .andExpect(jsonPath(ERROR_JPEXP).value(DiaryService.TITLE_MUST_NOT_BE_BLANK));

        verify(mockBookRepository, times(0)).save(any(Book.class));
    }

    @Test
    void getBooksSuccess() throws Exception {
        List<Book> books = new ArrayList<>();
        books.add(new Book(MOCK_BOOK_TITLE, mockUser));
        doReturn(books).when(mockBookRepository).findByUser(mockUser);

        mockMvcPerform(
            HttpMethod.GET, API_V1_DIARY,
            mockUser,
            HttpStatus.OK)
            .andExpect(jsonPath(MESSAGE_JPEXP).value(DiaryRestController.FETCHED_SUCCESSFULLY))
            .andExpect(jsonPath("$.data[0].title").value(MOCK_BOOK_TITLE));
    }

}
