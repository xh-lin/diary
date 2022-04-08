package com.xuhuang.diary.controllers.api.v1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.xuhuang.diary.models.Book;
import com.xuhuang.diary.models.Record;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    protected static final String MESSAGE_JPEXP = "$.message";

    private static final String TEXT = "text";
    private static final String TITLE = "title";

    private static final Long MOCK_BOOK_ID = 1L;
    private static final String MOCK_BOOK_TITLE = "Mock Diary";
    private static final Long NOT_FOUND_BOOK_ID = 2L;

    private static final Long MOCK_RECORD_ID = 1L;
    private static final String MOCK_RECORD_TEXT = "Mock Record";
    private static final Long NOT_FOUND_RECORD_ID = 2L;

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
        requestParams.add(TITLE, "My Diary");

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
        requestParams.add(TITLE, "");

        mockMvcPerform(
                HttpMethod.POST, API_V1_DIARY,
                requestParams, mockUser,
                HttpStatus.BAD_REQUEST)
                .andExpect(jsonPath(MESSAGE_JPEXP).value(DiaryService.TITLE_MUST_NOT_BE_BLANK));

        verify(mockBookRepository, times(0)).save(any(Book.class));
    }

    @Test
    void getBooksSuccess() throws Exception {
        setupMockRepository();

        mockMvcPerform(
                HttpMethod.GET, API_V1_DIARY,
                mockUser,
                HttpStatus.OK)
                .andExpect(jsonPath(MESSAGE_JPEXP).value(DiaryRestController.FETCHED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data[0].title").value(MOCK_BOOK_TITLE));
    }

    @Test
    void getBookSuccess() throws Exception {
        setupMockRepository();

        Object[] uriVars = { MOCK_BOOK_ID };

        mockMvcPerform(
                HttpMethod.GET, API_V1_DIARY_BOOKID,
                uriVars, mockUser,
                HttpStatus.OK)
                .andExpect(jsonPath(MESSAGE_JPEXP).value(DiaryRestController.FETCHED_SUCCESSFULLY))
                .andExpect(jsonPath("$.data.title").value(MOCK_BOOK_TITLE));
    }

    @Test
    void getBookFailure() throws Exception {
        setupMockRepository();

        // forbidden
        Object[] uriVars = { MOCK_BOOK_ID };

        mockMvcPerform(
                HttpMethod.GET, API_V1_DIARY_BOOKID,
                uriVars, anotherMockUser,
                HttpStatus.FORBIDDEN)
                .andExpect(jsonPath(MESSAGE_JPEXP).value(DiaryService.YOU_DO_NOT_HAVE_PERMISSION_TO_ACCESS));

        // not found
        uriVars[0] = NOT_FOUND_BOOK_ID;

        mockMvcPerform(
                HttpMethod.GET, API_V1_DIARY_BOOKID,
                uriVars, mockUser,
                HttpStatus.NOT_FOUND);
    }

    @Test
    void updateBookSuccess() throws Exception {
        setupMockRepository();

        Object[] uriVars = { MOCK_BOOK_ID };
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add(TITLE, "My New Diary");

        mockMvcPerform(
                HttpMethod.PUT, API_V1_DIARY_BOOKID,
                uriVars, requestParams, mockUser,
                HttpStatus.OK);

        verify(mockBookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void updateBookFailure() throws Exception {
        setupMockRepository();

        // forbidden
        Object[] uriVars = { MOCK_BOOK_ID };
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add(TITLE, "My New Diary");

        mockMvcPerform(
                HttpMethod.PUT, API_V1_DIARY_BOOKID,
                uriVars, requestParams, anotherMockUser,
                HttpStatus.FORBIDDEN);

        verify(mockBookRepository, times(0)).save(any(Book.class));

        // not found
        uriVars[0] = NOT_FOUND_BOOK_ID;

        mockMvcPerform(
                HttpMethod.PUT, API_V1_DIARY_BOOKID,
                uriVars, requestParams, mockUser,
                HttpStatus.NOT_FOUND);

        verify(mockBookRepository, times(0)).save(any(Book.class));

        // bad request
        uriVars[0] = MOCK_BOOK_ID;
        requestParams.clear();
        requestParams.add(TITLE, "");

        mockMvcPerform(
                HttpMethod.PUT, API_V1_DIARY_BOOKID,
                uriVars, requestParams, mockUser,
                HttpStatus.BAD_REQUEST);

        verify(mockBookRepository, times(0)).save(any(Book.class));
    }

    @Test
    void deleteBookSuccess() throws Exception {
        setupMockRepository();

        Object[] uriVars = { MOCK_BOOK_ID };

        mockMvcPerform(
                HttpMethod.DELETE, API_V1_DIARY_BOOKID,
                uriVars, mockUser,
                HttpStatus.OK);

        verify(mockBookRepository, times(1)).deleteById(any(Long.class));
    }

    @Test
    void deleteBookFailure() throws Exception {
        setupMockRepository();

        // forbidden
        Object[] uriVars = { MOCK_BOOK_ID };

        mockMvcPerform(
                HttpMethod.DELETE, API_V1_DIARY_BOOKID,
                uriVars, anotherMockUser,
                HttpStatus.FORBIDDEN);

        verify(mockBookRepository, times(0)).deleteById(any(Long.class));

        // not found
        uriVars[0] = NOT_FOUND_BOOK_ID;

        mockMvcPerform(
                HttpMethod.DELETE, API_V1_DIARY_BOOKID,
                uriVars, mockUser,
                HttpStatus.NOT_FOUND);

        verify(mockBookRepository, times(0)).deleteById(any(Long.class));
    }

    @Test
    void createRecordSuccess() throws Exception {
        setupMockRepository();

        Object[] uriVars = { MOCK_BOOK_ID };
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add(TEXT, "My Record");

        mockMvcPerform(
                HttpMethod.POST, API_V1_DIARY_BOOKID_RECORD,
                uriVars, requestParams, mockUser,
                HttpStatus.CREATED);

        verify(mockRecordRepository, times(1)).save(any(Record.class));
    }

    @Test
    void createRecordFailure() throws Exception {
        setupMockRepository();

        // forbidden
        Object[] uriVars = { MOCK_BOOK_ID };
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add(TEXT, "My Record");

        mockMvcPerform(
                HttpMethod.POST, API_V1_DIARY_BOOKID_RECORD,
                uriVars, requestParams, anotherMockUser,
                HttpStatus.FORBIDDEN);

        verify(mockRecordRepository, times(0)).save(any(Record.class));

        // not found
        uriVars[0] = NOT_FOUND_BOOK_ID;

        mockMvcPerform(
                HttpMethod.POST, API_V1_DIARY_BOOKID_RECORD,
                uriVars, requestParams, mockUser,
                HttpStatus.NOT_FOUND);

        verify(mockRecordRepository, times(0)).save(any(Record.class));
    }

    @Test
    void getRecordsSuccess() throws Exception {
        setupMockRepository();

        Object[] uriVars = { MOCK_BOOK_ID };

        mockMvcPerform(
                HttpMethod.GET, API_V1_DIARY_BOOKID_RECORD,
                uriVars, mockUser,
                HttpStatus.OK)
                .andExpect(jsonPath("$.data.content[0].text").value(MOCK_RECORD_TEXT));
    }

    @Test
    void getRecordsFailure() throws Exception {
        setupMockRepository();

        // forbidden
        Object[] uriVars = { MOCK_BOOK_ID };

        mockMvcPerform(
                HttpMethod.GET, API_V1_DIARY_BOOKID_RECORD,
                uriVars, anotherMockUser,
                HttpStatus.FORBIDDEN);

        // not found
        uriVars[0] = NOT_FOUND_BOOK_ID;

        mockMvcPerform(
                HttpMethod.GET, API_V1_DIARY_BOOKID_RECORD,
                uriVars, mockUser,
                HttpStatus.NOT_FOUND);

        // bad request page
        uriVars = new Object[] { MOCK_BOOK_ID, -1 };

        mockMvcPerform(
                HttpMethod.GET, API_V1_DIARY_BOOKID_RECORD_PAGE,
                uriVars, mockUser,
                HttpStatus.BAD_REQUEST)
                .andExpect(jsonPath(MESSAGE_JPEXP).value(DiaryService.PAGE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_ZERO));

        // bad request page size
        uriVars = new Object[] { MOCK_BOOK_ID, 0, 0 };

        mockMvcPerform(
                HttpMethod.GET, API_V1_DIARY_BOOKID_RECORD_PAGE_SIZE,
                uriVars, mockUser,
                HttpStatus.BAD_REQUEST)
                .andExpect(jsonPath(MESSAGE_JPEXP).value(DiaryService.SIZE_MUST_BE_GREATER_THAN_ZERO));
    }

    @Test
    void getRecordSuccess() throws Exception {
        setupMockRepository();

        Object[] uriVars = { MOCK_RECORD_ID };

        mockMvcPerform(
                HttpMethod.GET, API_V1_DIARY_RECORD_RECORDID,
                uriVars, mockUser,
                HttpStatus.OK)
                .andExpect(jsonPath("$.data.text").value(MOCK_RECORD_TEXT));
    }

    @Test
    void getRecordFailure() throws Exception {
        setupMockRepository();

        // forbidden
        Object[] uriVars = { MOCK_RECORD_ID };

        mockMvcPerform(
                HttpMethod.GET, API_V1_DIARY_RECORD_RECORDID,
                uriVars, anotherMockUser,
                HttpStatus.FORBIDDEN)
                .andExpect(jsonPath(MESSAGE_JPEXP).value(DiaryService.YOU_DO_NOT_HAVE_PERMISSION_TO_ACCESS));

        // not found
        uriVars[0] = NOT_FOUND_RECORD_ID;

        mockMvcPerform(
                HttpMethod.GET, API_V1_DIARY_RECORD_RECORDID,
                uriVars, mockUser,
                HttpStatus.NOT_FOUND);
    }

    @Test
    void updateRecordSuccess() throws Exception {
        setupMockRepository();

        Object[] uriVars = { MOCK_RECORD_ID };
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add(TEXT, "My New Record");

        mockMvcPerform(
                HttpMethod.PUT, API_V1_DIARY_RECORD_RECORDID,
                uriVars, requestParams, mockUser,
                HttpStatus.OK);

        verify(mockRecordRepository, times(1)).save(any(Record.class));
    }

    @Test
    void updateRecordFailure() throws Exception {
        setupMockRepository();

        // forbidden
        Object[] uriVars = { MOCK_RECORD_ID };
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add(TEXT, "My New Record");

        mockMvcPerform(
                HttpMethod.PUT, API_V1_DIARY_RECORD_RECORDID,
                uriVars, requestParams, anotherMockUser,
                HttpStatus.FORBIDDEN);

        verify(mockRecordRepository, times(0)).save(any(Record.class));

        // not found
        uriVars[0] = NOT_FOUND_RECORD_ID;

        mockMvcPerform(
                HttpMethod.PUT, API_V1_DIARY_RECORD_RECORDID,
                uriVars, requestParams, mockUser,
                HttpStatus.NOT_FOUND);

        verify(mockRecordRepository, times(0)).save(any(Record.class));
    }

    @Test
    void deleteRecordSuccess() throws Exception {
        setupMockRepository();

        Object[] uriVars = { MOCK_RECORD_ID };

        mockMvcPerform(
                HttpMethod.DELETE, API_V1_DIARY_RECORD_RECORDID,
                uriVars, mockUser,
                HttpStatus.OK);

        verify(mockRecordRepository, times(1)).deleteById(any(Long.class));
    }

    @Test
    void deleteRecordFailure() throws Exception {
        setupMockRepository();

        // forbidden
        Object[] uriVars = { MOCK_RECORD_ID };

        mockMvcPerform(
                HttpMethod.DELETE, API_V1_DIARY_RECORD_RECORDID,
                uriVars, anotherMockUser,
                HttpStatus.FORBIDDEN);

        verify(mockRecordRepository, times(0)).deleteById(any(Long.class));

        uriVars[0] = NOT_FOUND_RECORD_ID;

        mockMvcPerform(
                HttpMethod.DELETE, API_V1_DIARY_RECORD_RECORDID,
                uriVars, mockUser,
                HttpStatus.NOT_FOUND);

        verify(mockRecordRepository, times(0)).deleteById(any(Long.class));
    }

    private void setupMockRepository() {
        Pageable pageable = PageRequest.of(DiaryService.DEFAULT_PAGE, DiaryService.DEFAULT_PAGE_SIZE);

        // book of mockUser
        Book mockBook = new Book(MOCK_BOOK_TITLE, mockUser);
        mockBook.setId(MOCK_BOOK_ID);
        Optional<Book> optionalMockBook = Optional.ofNullable(mockBook);
        List<Book> mockBooks = new ArrayList<>();
        mockBooks.add(mockBook);

        doReturn(optionalMockBook).when(mockBookRepository).findById(MOCK_BOOK_ID);
        doReturn(mockBooks).when(mockBookRepository).findByUser(mockUser);

        // record of mockUser
        Record mockRecord = new Record(MOCK_RECORD_TEXT, mockBook);
        mockRecord.setId(MOCK_RECORD_ID);
        Optional<Record> optionalMockRecord = Optional.ofNullable(mockRecord);
        List<Record> mockRecords = Arrays.asList(mockRecord);
        Page<Record> mockRecordPage = new PageImpl<>(mockRecords, pageable, mockRecords.size());

        doReturn(optionalMockRecord).when(mockRecordRepository).findById(MOCK_RECORD_ID);
        doReturn(mockRecordPage).when(mockRecordRepository).findByBookOrderByCreatedAtDescIdDesc(mockBook, pageable);
    }

}
