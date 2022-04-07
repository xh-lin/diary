package com.xuhuang.diary.controllers.api.v1;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.security.auth.message.AuthException;

import com.xuhuang.diary.models.Book;
import com.xuhuang.diary.models.Record;
import com.xuhuang.diary.services.DiaryService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/v1/diary")
@RequiredArgsConstructor
public class DiaryRestController {

    public static final String CREATED_SUCCESSFULLY = "Created successfully.";
    public static final String FETCHED_SUCCESSFULLY = "Fetched successfully.";
    public static final String UPDATED_SUCCESSFULLY = "Updated successfully.";
    public static final String DELETED_SUCCESSFULLY = "Deleted successfully.";

    private static final String DATA = "data";
    private static final String MESSAGE = "message";
    private static final String ERROR = "error";

    private static final String PREV_PAGE_URL = "prevPageUrl";
    private static final String NEXT_PAGE_URL = "nextPageUrl";
    private static final String TOTAL_PAGES = "totalPages";
    private static final String PAGE_NUMBER = "pageNumber";
    private static final String PAGE_SIZE = "pageSize";
    private static final String CONTENT = "content";

    private final DiaryService diaryService;

    @PostMapping()
    public ResponseEntity<Object> createBook(@RequestParam String title) {
        Map<String, Object> body = new LinkedHashMap<>();
        Book book;

        try {
            book = diaryService.createBook(title);
        } catch (IllegalArgumentException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        body.put(DATA, book);
        body.put(MESSAGE, CREATED_SUCCESSFULLY);
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<Object> getBooks() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(DATA, diaryService.getBooks());
        body.put(MESSAGE, FETCHED_SUCCESSFULLY);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<Object> getBook(@PathVariable Long bookId) {
        Map<String, Object> body = new LinkedHashMap<>();
        Book book;

        try {
            book = diaryService.getBook(bookId);
        } catch (AuthException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
        } catch (NoSuchElementException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }

        body.put(DATA, book);
        body.put(MESSAGE, FETCHED_SUCCESSFULLY);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<Object> updateBook(@PathVariable Long bookId, @RequestParam String title) {
        Map<String, Object> body = new LinkedHashMap<>();
        Book book;

        try {
            book = diaryService.updateBook(bookId, title);
        } catch (AuthException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
        } catch (NoSuchElementException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        body.put(DATA, book);
        body.put(MESSAGE, UPDATED_SUCCESSFULLY);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Object> deleteBook(@PathVariable Long bookId) {
        Map<String, Object> body = new LinkedHashMap<>();
        Book book;

        try {
            book = diaryService.deleteBook(bookId);
        } catch (AuthException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
        } catch (NoSuchElementException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }

        body.put(DATA, book);
        body.put(MESSAGE, DELETED_SUCCESSFULLY);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/{bookId}/record")
    public ResponseEntity<Object> createRecord(@PathVariable Long bookId, @RequestParam String text) {
        Map<String, Object> body = new LinkedHashMap<>();
        Record recd;

        try {
            recd = diaryService.createRecord(bookId, text);
        } catch (AuthException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
        } catch (NoSuchElementException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }

        body.put(DATA, recd);
        body.put(MESSAGE, CREATED_SUCCESSFULLY);
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @GetMapping({
            "/{bookId}/record",
            "/{bookId}/record/{page}",
            "/{bookId}/record/{page}/{size}" })
    public ResponseEntity<Object> getRecords(
            @PathVariable Long bookId,
            @PathVariable(required = false) Integer page,
            @PathVariable(required = false) Integer size) {
        Map<String, Object> body = new LinkedHashMap<>();
        Page<Record> recordPage;

        try {
            if (page == null) {
                recordPage = diaryService.getRecords(bookId);
            } else if (size == null) {
                recordPage = diaryService.getRecords(bookId, page);
            } else {
                recordPage = diaryService.getRecords(bookId, page, size);
            }
        } catch (AuthException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
        } catch (NoSuchElementException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        int totalPages = recordPage.getTotalPages();
        page = recordPage.getPageable().getPageNumber();
        size = recordPage.getPageable().getPageSize();
        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/diary").pathSegment("{bookId}", "record", "{page}", "{size}");
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
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/record/{recordId}")
    public ResponseEntity<Object> getRecord(@PathVariable Long recordId) {
        Map<String, Object> body = new LinkedHashMap<>();
        Record recd;

        try {
            recd = diaryService.getRecord(recordId);
        } catch (AuthException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
        } catch (NoSuchElementException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }

        body.put(DATA, recd);
        body.put(MESSAGE, FETCHED_SUCCESSFULLY);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PutMapping("/record/{recordId}")
    public ResponseEntity<Object> updateRecord(@PathVariable Long recordId, @RequestParam String text) {
        Map<String, Object> body = new LinkedHashMap<>();
        Record recd;

        try {
            recd = diaryService.updateRecord(recordId, text);
        } catch (AuthException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
        } catch (NoSuchElementException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }

        body.put(DATA, recd);
        body.put(MESSAGE, UPDATED_SUCCESSFULLY);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @DeleteMapping("/record/{recordId}")
    public ResponseEntity<Object> deleteRecord(@PathVariable Long recordId) {
        Map<String, Object> body = new LinkedHashMap<>();
        Record recd;

        try {
            recd = diaryService.deleteRecord(recordId);
        } catch (AuthException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
        } catch (NoSuchElementException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }

        body.put(DATA, recd);
        body.put(MESSAGE, DELETED_SUCCESSFULLY);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}
