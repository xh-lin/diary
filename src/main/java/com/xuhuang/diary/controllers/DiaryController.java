package com.xuhuang.diary.controllers;

import static com.xuhuang.diary.utils.Utils.asTimestamp;

import java.sql.Timestamp;
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
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

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
        if (page == null) page = DEFAULT_PAGE;
        if (size == null) size = DEFAULT_SIZE;

        List<Book> books = diaryService.getBooks();
        Page<Record> recordPage = null;

        // if have not selected a book and there are books
        if (bookId.equals(BOOK_ID_UNDEFINED) && !books.isEmpty()) {
            bookId = books.get(0).getId(); // default is the first book
        }

        // if selected a book
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
        model.addAttribute(RECORDS, recordPage == null ? new ArrayList<>() : recordPage.getContent());
        model.addAttribute(TOTAL_PAGES, recordPage == null ? -1 : recordPage.getTotalPages());
        model.addAttribute(PAGE_NUMBER, page);
        model.addAttribute(PAGE_SIZE, size);
        return Template.DIARY.toString();
    }

    @PostMapping("/fragments/book")
    public String loadBookFragment(
            Model model,
            @RequestParam(required = false) Long currentBookId,
            @RequestBody Object book) {
        // default variables
        if (currentBookId == null) currentBookId = BOOK_ID_UNDEFINED;

        model.addAttribute(CURRENT_BOOK_ID, currentBookId);
        model.addAttribute(BOOK, book);
        return "fragments/diary::book";
    }

    @PostMapping("/fragments/records")
    public String loadRecordsFragment(Model model, @RequestBody List<Map<String, Object>> recordsJson) {
        List<Record> records = new ArrayList<>();

        for (Map<String, Object> recordJson : recordsJson) {
            Record recd = new Record();
            Long id = Long.valueOf(((Integer) recordJson.get("id")).longValue());
            Timestamp createdAt = asTimestamp((String) recordJson.get("createdAt"));
            Timestamp updateddAt = asTimestamp((String) recordJson.get("updatedAt"));
            Long bookId = Long.valueOf(((Integer) recordJson.get("book_id")).longValue());
            Book book;
            try {
                book = diaryService.getBook(bookId);
            } catch (AuthException e) {
                book = null;
            }

            recd.setId(id);
            recd.setCreatedAt(createdAt);
            recd.setUpdatedAt(updateddAt);
            recd.setText((String) recordJson.get("text"));
            recd.setBook(book);
            records.add(recd);
        }

        model.addAttribute(RECORDS, records);
        return "fragments/diary::records";
    }

}
