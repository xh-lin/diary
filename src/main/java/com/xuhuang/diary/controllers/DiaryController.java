package com.xuhuang.diary.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.security.auth.message.AuthException;

import com.xuhuang.diary.models.Book;
import com.xuhuang.diary.models.Record;
import com.xuhuang.diary.services.DiaryService;
import com.xuhuang.diary.services.UserService;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/diary")
@RequiredArgsConstructor
public class DiaryController {

    private static final String REDIRECT_ERROR_400 = "redirect:/error/400";
    private static final String REDIRECT_ERROR_404 = "redirect:/error/404";
    private static final String REDIRECT_ERROR_403 = "redirect:/error/403";

    private static final Long BOOK_ID_UNDEFINED = -1L;

    private static final String USER = "user";
    private static final String CURRENT_BOOK_ID = "currentBookId";
    private static final String BOOKS = "books";
    private static final String RECORDS = "records";
    private static final String TOTAL_PAGES = "totalPages";
    private static final String PAGE_NUMBER = "pageNumber";
    private static final String PAGE_SIZE = "pageSize";
    private static final String BOOK = "book";

    private final UserService userService;
    private final DiaryService diaryService;

    @GetMapping({
        "",
        "/{bookId}",
        "/{bookId}/record/{page}",
        "/{bookId}/record/{page}/{size}"})
    public String viewDiary(
            Model model,
            @PathVariable(required = false) Long bookId,
            @PathVariable(required = false) Integer page,
            @PathVariable(required = false) Integer size) {
        // default variables
        if (bookId == null) bookId = BOOK_ID_UNDEFINED;

        List<Book> books = diaryService.getBooks();

        // if have not selected a book and there are books
        if (bookId.equals(BOOK_ID_UNDEFINED) && !books.isEmpty()) {
            bookId = books.get(0).getId(); // default is the first book
        }

        // if selected a book
        if (!bookId.equals(BOOK_ID_UNDEFINED)) {
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
                return REDIRECT_ERROR_403;
            } catch (NoSuchElementException e) {
                return REDIRECT_ERROR_404;
            } catch (IllegalArgumentException e) {
                return REDIRECT_ERROR_400;
            }
            model.addAttribute(RECORDS, recordPage.getContent());
            model.addAttribute(TOTAL_PAGES, recordPage.getTotalPages());
            model.addAttribute(PAGE_NUMBER, recordPage.getPageable().getPageNumber());
            model.addAttribute(PAGE_SIZE, recordPage.getPageable().getPageSize());
        }

        model.addAttribute(USER, userService.getCurrentUser());
        model.addAttribute(CURRENT_BOOK_ID, bookId);
        model.addAttribute(BOOKS, books);
        return Template.DIARY.toString();
    }

    @PostMapping("/fragments/book")
    public String loadBookFragment(
            Model model,
            @RequestParam(required = false) Long currentBookId,
            @RequestBody Map<String, Object> book) {
        // default variables
        if (currentBookId == null) currentBookId = BOOK_ID_UNDEFINED;

        model.addAttribute(CURRENT_BOOK_ID, currentBookId);
        model.addAttribute(BOOK, diaryService.parseBookJson(book));
        return "fragments/diary::book";
    }

    @PostMapping("/fragments/records")
    public String loadRecordsFragment(Model model, @RequestBody List<Map<String, Object>> recordsJson) {
        List<Record> records = new ArrayList<>();

        for (Map<String, Object> recordJson : recordsJson) {
            records.add(diaryService.parseRecordJson(recordJson));
        }

        model.addAttribute(RECORDS, records);
        return "fragments/diary::records";
    }

}
