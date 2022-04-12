package com.xuhuang.diary.controllers.view.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.message.AuthException;

import com.xuhuang.diary.controllers.view.Template;
import com.xuhuang.diary.models.Book;
import com.xuhuang.diary.models.Record;
import com.xuhuang.diary.models.Tag;
import com.xuhuang.diary.services.DiaryService;
import com.xuhuang.diary.services.TagService;
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

    private static final String USER = "user";
    private static final String CURRENT_BOOK_ID = "currentBookId";
    private static final String BOOKS = "books";
    private static final String RECORDS = "records";
    private static final String TAGS = "tags";
    private static final String NEXT_PAGE_URL = "nextPageUrl";

    private final UserService userService;
    private final DiaryService diaryService;
    private final TagService tagService;

    @GetMapping({
            "",
            "/{bookId}",
            "/{bookId}/record/{page}",
            "/{bookId}/record/{page}/{size}" })
    public String viewDiary(
            Model model,
            @PathVariable(required = false) Long bookId,
            @PathVariable(required = false) Integer page,
            @PathVariable(required = false) Integer size) throws AuthException {
        List<Book> books = diaryService.getBooks();

        // if have not selected a book and there are books
        if (bookId == null && !books.isEmpty()) {
            bookId = books.get(0).getId(); // default is the first book
        }

        // if selected a book
        if (bookId != null) {
            Page<Record> recordPage;

            if (page == null) {
                recordPage = diaryService.getRecords(bookId);
            } else if (size == null) {
                recordPage = diaryService.getRecords(bookId, page);
            } else {
                recordPage = diaryService.getRecords(bookId, page, size);
            }

            int totalPages = recordPage.getTotalPages();
            page = recordPage.getPageable().getPageNumber();
            size = recordPage.getPageable().getPageSize();
            int nextPage = Math.max(page + 1, 0);
            String nextPageUrl = String.format("/api/v1/diary/%d/record/%d/%d", bookId, nextPage, size);

            model.addAttribute(RECORDS, recordPage.getContent());
            model.addAttribute(NEXT_PAGE_URL, (nextPage < totalPages) ? nextPageUrl : null);
            model.addAttribute(TAGS, tagService.getTags());
        }

        model.addAttribute(USER, userService.getCurrentUser());
        model.addAttribute(CURRENT_BOOK_ID, bookId);
        model.addAttribute(BOOKS, books);
        return Template.DIARY.toString();
    }

    @PostMapping("/fragments/books")
    public String loadBooksFragment(Model model, @RequestParam(required = false) Long currentBookId,
            @RequestBody List<Map<String, Object>> booksJson) {
        List<Book> books = new ArrayList<>();

        for (Map<String, Object> bookJson : booksJson) {
            books.add(diaryService.parseBookJson(bookJson));
        }

        model.addAttribute(CURRENT_BOOK_ID, currentBookId);
        model.addAttribute(BOOKS, books);
        return "fragments/diary::books";
    }

    @PostMapping("/fragments/records")
    public String loadRecordsFragment(Model model, @RequestBody List<Map<String, Object>> recordsJson) {
        List<Record> records = new ArrayList<>();

        for (Map<String, Object> recordJson : recordsJson) {
            records.add(diaryService.parseRecordJson(recordJson));
        }

        model.addAttribute(RECORDS, records);
        model.addAttribute(TAGS, tagService.getTags());
        return "fragments/record::records";
    }

    @PostMapping("/fragments/tags")
    public String loadTagsFragment(Model model, @RequestBody List<Map<String, Object>> tagsJson) {
        List<Tag> tags = new ArrayList<>();

        for (Map<String, Object> tagJson : tagsJson) {
            tags.add(tagService.parseTagJson(tagJson));
        }

        model.addAttribute(TAGS, tags);
        return "fragments/tag::tags";
    }

}
