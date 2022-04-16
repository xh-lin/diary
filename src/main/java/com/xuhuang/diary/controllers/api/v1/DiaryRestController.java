package com.xuhuang.diary.controllers.api.v1;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.security.auth.message.AuthException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.xuhuang.diary.models.Book;
import com.xuhuang.diary.models.Record;
import com.xuhuang.diary.models.Tag;
import com.xuhuang.diary.services.BookService;
import com.xuhuang.diary.services.RecordService;
import com.xuhuang.diary.services.TagService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/diary")
@RequiredArgsConstructor
public class DiaryRestController {

    public static final String CREATED_SUCCESSFULLY = "Created successfully.";
    public static final String FETCHED_SUCCESSFULLY = "Fetched successfully.";
    public static final String UPDATED_SUCCESSFULLY = "Updated successfully.";
    public static final String DELETED_SUCCESSFULLY = "Deleted successfully.";

    public static final String TITLE_NOTBLANK = "Title must not be blank.";
    public static final String TEXT_NOT_EMPTY = "Text must not be empty.";
    public static final String PAGE_MIN = "Page must be greater than or equal to zero.";
    public static final String SIZE_MIN = "Size must be greater than zero.";

    private static final String DATA = "data";
    private static final String MESSAGE = "message";
    private static final String RECORD = "record";
    private static final String TAG = "tag";

    private static final String PREV_PAGE_URL = "prevPageUrl";
    private static final String NEXT_PAGE_URL = "nextPageUrl";
    private static final String TOTAL_PAGES = "totalPages";
    private static final String PAGE_NUMBER = "pageNumber";
    private static final String PAGE_SIZE = "pageSize";
    private static final String CONTENT = "content";

    private final BookService bookService;
    private final RecordService recordService;
    private final TagService tagService;

    @PostMapping()
    public ResponseEntity<Object> createBook(@RequestParam @NotBlank(message = TITLE_NOTBLANK) String title) {
        log.info("createBook(title: {})", title);

        Map<String, Object> body = new LinkedHashMap<>();
        Book book = bookService.createBook(title);
        body.put(DATA, book);
        body.put(MESSAGE, CREATED_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.CREATED);
        log.info("{}", response);
        return response;
    }

    @GetMapping()
    public ResponseEntity<Object> getBooks() {
        log.info("getBooks()");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put(DATA, bookService.getBooks());
        body.put(MESSAGE, FETCHED_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.OK);
        log.info("{}", response);
        return response;
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<Object> getBook(@PathVariable Long bookId) throws AuthException {
        log.info("getBook(bookId: {})", bookId);

        Map<String, Object> body = new LinkedHashMap<>();
        Book book = bookService.getBook(bookId);
        body.put(DATA, book);
        body.put(MESSAGE, FETCHED_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.OK);
        log.info("{}", response);
        return response;
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<Object> updateBook(@PathVariable Long bookId,
            @RequestParam @NotBlank(message = TITLE_NOTBLANK) String title) throws AuthException {
        log.info("updateBook(bookId: {}, title: {})", bookId, title);

        Map<String, Object> body = new LinkedHashMap<>();
        Book book = bookService.updateBook(bookId, title);
        body.put(DATA, book);
        body.put(MESSAGE, UPDATED_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.OK);
        log.info("{}", response);
        return response;
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Object> deleteBook(@PathVariable Long bookId) throws AuthException {
        log.info("deleteBook(bookId: {})", bookId);

        Map<String, Object> body = new LinkedHashMap<>();
        Book book = bookService.deleteBook(bookId);
        body.put(DATA, book);
        body.put(MESSAGE, DELETED_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.OK);
        log.info("{}", response);
        return response;
    }

    @PostMapping("/{bookId}/record")
    public ResponseEntity<Object> createRecord(@PathVariable Long bookId,
            @RequestParam @NotEmpty(message = TEXT_NOT_EMPTY) String text) throws AuthException {
        log.info("createRecord(bookId: {}, text: {})", bookId, text);

        Map<String, Object> body = new LinkedHashMap<>();
        Record recd = recordService.createRecord(bookId, text);
        body.put(DATA, recd);
        body.put(MESSAGE, CREATED_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.CREATED);
        log.info("{}", response);
        return response;
    }

    @GetMapping({
            "/{bookId}/record",
            "/{bookId}/record/{page}",
            "/{bookId}/record/{page}/{size}" })
    public ResponseEntity<Object> getRecords(@PathVariable Long bookId,
            @PathVariable(required = false) @Min(value = 0, message = PAGE_MIN) Integer page,
            @PathVariable(required = false) @Min(value = 1, message = SIZE_MIN) Integer size) throws AuthException {
        log.info("getRecords(bookId: {}, page: {}, size: {})", bookId, page, size);

        Map<String, Object> body = new LinkedHashMap<>();
        Page<Record> recordPage;

        if (page == null) {
            recordPage = recordService.getRecords(bookId);
        } else if (size == null) {
            recordPage = recordService.getRecords(bookId, page);
        } else {
            recordPage = recordService.getRecords(bookId, page, size);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        int totalPages = recordPage.getTotalPages();
        page = recordPage.getPageable().getPageNumber();
        size = recordPage.getPageable().getPageSize();
        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/diary").pathSegment("{bookId}", RECORD, "{page}", "{size}");
        int prevPage = Math.min(page - 1, totalPages - 1);
        int nextPage = Math.max(page + 1, 0);

        data.put(PREV_PAGE_URL, (prevPage >= 0) ? uriBuilder.build(bookId, prevPage, size) : null);
        data.put(NEXT_PAGE_URL, (nextPage < totalPages) ? uriBuilder.build(bookId, nextPage, size) : null);
        data.put(TOTAL_PAGES, totalPages);
        data.put(PAGE_NUMBER, page);
        data.put(PAGE_SIZE, size);
        data.put(CONTENT, recordPage.getContent());

        body.put(DATA, data);
        body.put(MESSAGE, FETCHED_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.OK);
        log.info("{}", response);
        return response;
    }

    @GetMapping("/record/{recordId}")
    public ResponseEntity<Object> getRecord(@PathVariable Long recordId) throws AuthException {
        log.info("getRecord(recordId: {})", recordId);

        Map<String, Object> body = new LinkedHashMap<>();
        Record recd = recordService.getRecord(recordId);
        body.put(DATA, recd);
        body.put(MESSAGE, FETCHED_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.OK);
        log.info("{}", response);
        return response;
    }

    @PutMapping("/record/{recordId}")
    public ResponseEntity<Object> updateRecord(@PathVariable Long recordId,
            @RequestParam @NotEmpty(message = TEXT_NOT_EMPTY) String text) throws AuthException {
        log.info("updateRecord(recordId: {}, text: {})", recordId, text);

        Map<String, Object> body = new LinkedHashMap<>();
        Record recd = recordService.updateRecord(recordId, text);
        body.put(DATA, recd);
        body.put(MESSAGE, UPDATED_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.OK);
        log.info("{}", response);
        return response;
    }

    @DeleteMapping("/record/{recordId}")
    public ResponseEntity<Object> deleteRecord(@PathVariable Long recordId) throws AuthException {
        log.info("deleteRecord(recordId: {})", recordId);

        Map<String, Object> body = new LinkedHashMap<>();
        Record recd = recordService.deleteRecord(recordId);
        body.put(DATA, recd);
        body.put(MESSAGE, DELETED_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.OK);
        log.info("{}", response);
        return response;
    }

    @PutMapping("/record/{recordId}/tag/{tagId}")
    public ResponseEntity<Object> addTag(@PathVariable Long recordId, @PathVariable Long tagId) throws AuthException {
        log.info("addTag(recordId: {}, tagId: {})", recordId, tagId);

        Map<String, Object> body = new LinkedHashMap<>();
        Map<String, Object> data = new LinkedHashMap<>();
        Record recd = recordService.addTag(recordId, tagId);
        Tag tag = tagService.getTag(tagId);

        data.put(RECORD, recd);
        data.put(TAG, tag);
        body.put(DATA, data);
        body.put(MESSAGE, UPDATED_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.OK);
        log.info("{}", response);
        return response;
    }

    @DeleteMapping("/record/{recordId}/tag/{tagId}")
    public ResponseEntity<Object> removeTag(@PathVariable Long recordId, @PathVariable Long tagId)
            throws AuthException {
        log.info("removeTag(recordId: {}, tagId: {})", recordId, tagId);

        Map<String, Object> body = new LinkedHashMap<>();
        Map<String, Object> data = new LinkedHashMap<>();
        Record recd = recordService.removeTag(recordId, tagId);
        Tag tag = tagService.getTag(tagId);

        data.put(RECORD, recd);
        data.put(TAG, tag);
        body.put(DATA, data);
        body.put(MESSAGE, DELETED_SUCCESSFULLY);

        ResponseEntity<Object> response = new ResponseEntity<>(body, HttpStatus.OK);
        log.info("{}", response);
        return response;
    }

}
