package com.xuhuang.diary.controllers.api.v1;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.security.auth.message.AuthException;

import com.xuhuang.diary.models.Book;
import com.xuhuang.diary.models.Record;
import com.xuhuang.diary.services.DiaryService;

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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/diary")
@RequiredArgsConstructor
public class DiaryRestController {

    public static final String CREATED_SUCCESSFULLY = "Created successfully.";
    public static final String FETCHED_SUCCESSFULLY = "Fetched successfully.";
    public static final String UPDATED_SUCCESSFULLY = "Updated successfully.";
    public static final String DELETED_SUCCESSFULLY = "Deleted successfully.";

    private static final String DATA = "data";
    private static final String MESSAGE = "message";
    private static final String ERROR = "error";

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
        return new ResponseEntity<>(body, HttpStatus.OK);
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
            return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
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
            return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
        } catch (NoSuchElementException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
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
            return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
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
        Record record;

        try {
            record = diaryService.createRecord(bookId, text);
        } catch (AuthException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
        } catch (NoSuchElementException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }

        body.put(DATA, record);
        body.put(MESSAGE, CREATED_SUCCESSFULLY);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/record/{recordId}")
    public ResponseEntity<Object> getRecord(@PathVariable Long recordId) {
        Map<String, Object> body = new LinkedHashMap<>();
        Record record;

        try {
            record = diaryService.getRecord(recordId);
        } catch (AuthException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
        } catch (NoSuchElementException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }

        body.put(DATA, record);
        body.put(MESSAGE, FETCHED_SUCCESSFULLY);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PutMapping("/record/{recordId}")
    public ResponseEntity<Object> updateRecord(@PathVariable Long recordId, @RequestParam String text) {
        Map<String, Object> body = new LinkedHashMap<>();
        Record record;

        try {
            record = diaryService.updateRecord(recordId, text);
        } catch (AuthException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
        } catch (NoSuchElementException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }

        body.put(DATA, record);
        body.put(MESSAGE, UPDATED_SUCCESSFULLY);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @DeleteMapping("/record/{recordId}")
    public ResponseEntity<Object> deleteRecord(@PathVariable Long recordId) {
        Map<String, Object> body = new LinkedHashMap<>();
        Record record;

        try {
            record = diaryService.deleteRecord(recordId);
        } catch (AuthException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
        } catch (NoSuchElementException e) {
            body.put(ERROR, e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }

        body.put(DATA, record);
        body.put(MESSAGE, DELETED_SUCCESSFULLY);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}
