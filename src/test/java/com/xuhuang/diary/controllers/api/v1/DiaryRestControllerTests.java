package com.xuhuang.diary.controllers.api.v1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private static final Long MOCK_BOOK_ID = 1L;
    private static final String MOCK_BOOK_TITLE = "Mock Diary";
    private static final Long ANOTHER_MOCK_BOOK_ID = 2L;
    private static final String ANOTHER_MOCK_BOOK_TITLE = "Another Mock Diary";
    private static final Long NOT_FOUND_BOOK_ID = 3L;

    private static User mockUser;
    private static User anotherMockUser;

    @MockBean
    private UserRepository mockUserRepository;
    @MockBean
    private BookRepository mockBookRepository;
    @MockBean
    private RecordRepository mockRecordRepository;

    @BeforeAll
    static void setup() {
        mockUser = mockUser(1L);
        anotherMockUser = mockUser(2L);
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
        setupMockBookRepository();

        mockMvcPerform(
            HttpMethod.GET, API_V1_DIARY,
            mockUser,
            HttpStatus.OK)
            .andExpect(jsonPath(MESSAGE_JPEXP).value(DiaryRestController.FETCHED_SUCCESSFULLY))
            .andExpect(jsonPath("$.data[0].title").value(MOCK_BOOK_TITLE));
    }

    @Test
    void getBookSuccess() throws Exception {
        setupMockBookRepository();

        Object[] uriVars = {MOCK_BOOK_ID};

        mockMvcPerform(
            HttpMethod.GET, API_V1_DIARY_BOOKID,
            uriVars, mockUser,
            HttpStatus.OK)
            .andExpect(jsonPath(MESSAGE_JPEXP).value(DiaryRestController.FETCHED_SUCCESSFULLY))
            .andExpect(jsonPath("$.data.title").value(MOCK_BOOK_TITLE));
    }

    @Test
    void getBookFailure() throws Exception {
        setupMockBookRepository();

        // forbidden
        Object[] uriVars = {ANOTHER_MOCK_BOOK_ID};

        mockMvcPerform(
            HttpMethod.GET, API_V1_DIARY_BOOKID,
            uriVars, mockUser,
            HttpStatus.FORBIDDEN)
            .andExpect(jsonPath(ERROR_JPEXP).value(DiaryService.YOU_DO_NOT_HAVE_PERMISSION_TO_ACCESS));

        // not found
        uriVars = new Object[] {NOT_FOUND_BOOK_ID};

        mockMvcPerform(
            HttpMethod.GET, API_V1_DIARY_BOOKID,
            uriVars, mockUser,
            HttpStatus.NOT_FOUND);
    }

    @Test
    void updateBookSuccess() throws Exception {
        setupMockBookRepository();

        Object[] uriVars = {MOCK_BOOK_ID};
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("title", "My New Diary");

        mockMvcPerform(
            HttpMethod.PUT, API_V1_DIARY_BOOKID,
            uriVars, requestParams, mockUser,
            HttpStatus.OK);

        verify(mockBookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void updateBookFailure() throws Exception {
        setupMockBookRepository();

        // forbidden
        Object[] uriVars = {ANOTHER_MOCK_BOOK_ID};
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("title", "My New Diary");

        mockMvcPerform(
            HttpMethod.PUT, API_V1_DIARY_BOOKID,
            uriVars, requestParams, mockUser,
            HttpStatus.FORBIDDEN);

        verify(mockBookRepository, times(0)).save(any(Book.class));

        // not found
        uriVars = new Object[] {NOT_FOUND_BOOK_ID};

        mockMvcPerform(
            HttpMethod.PUT, API_V1_DIARY_BOOKID,
            uriVars, requestParams, mockUser,
            HttpStatus.NOT_FOUND);

        verify(mockBookRepository, times(0)).save(any(Book.class));

        // bad request
        uriVars = new Object[] {MOCK_BOOK_ID};
        requestParams.clear();
        requestParams.add("title", "");

        mockMvcPerform(
            HttpMethod.PUT, API_V1_DIARY_BOOKID,
            uriVars, requestParams, mockUser,
            HttpStatus.BAD_REQUEST);

        verify(mockBookRepository, times(0)).save(any(Book.class));
    }

    @Test
    void deleteBookSuccess() throws Exception {
        setupMockBookRepository();

        Object[] uriVars = {MOCK_BOOK_ID};

        mockMvcPerform(
            HttpMethod.DELETE, API_V1_DIARY_BOOKID,
            uriVars, mockUser,
            HttpStatus.OK);

        verify(mockBookRepository, times(1)).deleteById(any(Long.class));
    }

    @Test
    void deleteBookFailure() throws Exception {
        setupMockBookRepository();

        // forbidden
        Object[] uriVars = {ANOTHER_MOCK_BOOK_ID};

        mockMvcPerform(
            HttpMethod.DELETE, API_V1_DIARY_BOOKID,
            uriVars, mockUser,
            HttpStatus.FORBIDDEN);

        verify(mockBookRepository, times(0)).deleteById(any(Long.class));

        // not found
        uriVars = new Object[] {NOT_FOUND_BOOK_ID};

        mockMvcPerform(
            HttpMethod.DELETE, API_V1_DIARY_BOOKID,
            uriVars, mockUser,
            HttpStatus.NOT_FOUND);

        verify(mockBookRepository, times(0)).deleteById(any(Long.class));
    }

    private void setupMockBookRepository() {
        // book of mockUser
        Book mockBook = new Book(MOCK_BOOK_TITLE, mockUser);
        mockBook.setId(MOCK_BOOK_ID);
        List<Book> mockBooks = new ArrayList<>();
        mockBooks.add(mockBook);
        Optional<Book> optionalMockBook = Optional.ofNullable(mockBook);

        doReturn(mockBooks).when(mockBookRepository).findByUser(mockUser);
        doReturn(optionalMockBook).when(mockBookRepository).findById(MOCK_BOOK_ID);

        // book of anotherMockUser
        Book anotherMockBook = new Book(ANOTHER_MOCK_BOOK_TITLE, anotherMockUser);
        anotherMockBook.setId(ANOTHER_MOCK_BOOK_ID);
        List<Book> anotherMockBooks = new ArrayList<>();
        anotherMockBooks.add(anotherMockBook);
        Optional<Book> optionalAnotherMockBook = Optional.ofNullable(anotherMockBook);

        doReturn(anotherMockBooks).when(mockBookRepository).findByUser(anotherMockUser);
        doReturn(optionalAnotherMockBook).when(mockBookRepository).findById(ANOTHER_MOCK_BOOK_ID);
    }

}
