package com.xuhuang.diary.controllers;

import java.util.List;
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

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/diary")
@RequiredArgsConstructor
public class DiaryController {

    private static final String REDIRECT_ERROR_400 = "redirect:/error/400";
    private static final String REDIRECT_ERROR_404 = "redirect:/error/404";
    private static final String REDIRECT_ERROR_403 = "redirect:/error/403";

    private static final Long BOOK_ID_UNDEFINED = -1L;
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    private static final String USER = "user";
    private static final String CURRENT_BOOK_ID = "currentBookId";
    private static final String BOOKS = "books";
    private static final String RECORDS = "records";
    private static final String TOTAL_PAGES = "totalPages";

    private final UserService userService;
    private final DiaryService diaryService;

    @GetMapping("/{bookId}/record/{page}/{size}")
    public String viewDiary(Model model, @PathVariable Long bookId, @PathVariable int page, @PathVariable int size) {
        List<Book> books = diaryService.getBooks();
        Page<Record> recordPage = null;

        if (bookId.equals(BOOK_ID_UNDEFINED) && !books.isEmpty()) {
            bookId = books.get(0).getId(); // default is the first book
        }

        if (!bookId.equals(BOOK_ID_UNDEFINED)) {
            try {
                recordPage = diaryService.getRecords(bookId, page, size);
            } catch (AuthException e) {
                return REDIRECT_ERROR_403;
            } catch (NoSuchElementException e) {
                return REDIRECT_ERROR_404;
            } catch (IllegalArgumentException e) {
                return REDIRECT_ERROR_400;
            }
        }

        model.addAttribute(USER, userService.getCurrentUser());
        model.addAttribute(CURRENT_BOOK_ID, bookId);
        model.addAttribute(BOOKS, books);
        model.addAttribute(RECORDS, recordPage == null ? null : recordPage.getContent());
        model.addAttribute(TOTAL_PAGES, recordPage == null ? null : recordPage.getTotalPages());
        return Template.DIARY.toString();
    }

    @GetMapping("/{bookId}/record/{page}")
    public String viewDiary(Model model, @PathVariable Long bookId, @PathVariable int page) {
        return viewDiary(model, bookId, page, DEFAULT_SIZE);
    }

    @GetMapping("/{bookId}")
    public String viewDiary(Model model, @PathVariable Long bookId) {
        return viewDiary(model, bookId, DEFAULT_PAGE, DEFAULT_SIZE);
    }

    @GetMapping
    public String viewDiary(Model model) {
        return viewDiary(model, BOOK_ID_UNDEFINED, DEFAULT_PAGE, DEFAULT_SIZE);
    }

    @PostMapping("/fragments/book")
    public String loadBookFragment(Model model, @RequestBody Object book) {
        model.addAttribute("book", book);
        return "fragments/diary::book";
    }

}