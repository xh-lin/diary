package com.xuhuang.diary.controllers.view.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.message.AuthException;

import com.xuhuang.diary.controllers.view.Template;
import com.xuhuang.diary.models.Book;
import com.xuhuang.diary.models.Record;
import com.xuhuang.diary.models.Tag;
import com.xuhuang.diary.services.BookService;
import com.xuhuang.diary.services.RecordService;
import com.xuhuang.diary.services.TagService;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/diary")
@RequiredArgsConstructor
public class DiaryController {

    private static final String TITLE_MAX_LENGTH = "titleMaxLength";
    private static final String TEXT_MAX_LENGTH = "textMaxLength";
    private static final String NAME_MAX_LENGTH = "nameMaxLength";

    private static final String CURRENT_BOOK_ID = "currentBookId";
    private static final String BOOKS = "books";
    private static final String RECORDS = "records";
    private static final String TAGS = "tags";
    private static final String NEXT_PAGE_URL = "nextPageUrl";

    private final BookService bookService;
    private final RecordService recordService;
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
        log.info("viewDiary(bookId: {}, page: {}, size: {})", bookId, page, size);

        List<Book> books = bookService.getBooks();

        // if have not selected a book and there are books
        if (bookId == null && !books.isEmpty()) {
            bookId = books.get(0).getId(); // default is the first book
        }

        // if selected a book
        if (bookId != null) {
            Page<Record> recordPage;

            if (page == null) {
                recordPage = recordService.getRecords(bookId);
            } else if (size == null) {
                recordPage = recordService.getRecords(bookId, page);
            } else {
                recordPage = recordService.getRecords(bookId, page, size);
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

        model.addAttribute(TITLE_MAX_LENGTH, Book.TITLE_LENGTH);
        model.addAttribute(TEXT_MAX_LENGTH, Record.TEXT_LENGTH);
        model.addAttribute(NAME_MAX_LENGTH, Tag.NAME_LENGTH);
        model.addAttribute(CURRENT_BOOK_ID, bookId);
        model.addAttribute(BOOKS, books);

        log.info("{}", model.asMap());
        return Template.DIARY.toString();
    }

    @PostMapping("/fragments/books")
    public String loadBooksFragment(Model model, @RequestParam(required = false) Long currentBookId,
            @RequestBody List<Map<String, Object>> booksJson) {
        log.info("loadBooksFragment(currentBookId: {}, booksJson: {})", currentBookId, booksJson);

        List<Book> books = new ArrayList<>();
        for (Map<String, Object> bookJson : booksJson) {
            books.add(bookService.parseBookJson(bookJson));
        }

        model.addAttribute(CURRENT_BOOK_ID, currentBookId);
        model.addAttribute(BOOKS, books);

        log.info("{}", model.asMap());
        return "fragments/book::books";
    }

    @PostMapping("/fragments/records")
    public String loadRecordsFragment(Model model, @RequestBody List<Map<String, Object>> recordsJson) {
        log.info("loadRecordsFragment(recordsJson: {})", recordsJson);

        List<Record> records = new ArrayList<>();
        for (Map<String, Object> recordJson : recordsJson) {
            records.add(recordService.parseRecordJson(recordJson));
        }

        model.addAttribute(RECORDS, records);
        model.addAttribute(TAGS, tagService.getTags());

        log.info("{}", model.asMap());
        return "fragments/record::records";
    }

    @PostMapping("/fragments/tag_buttons")
    public String loadTagButtonsFragment(Model model, @RequestBody List<Map<String, Object>> tagsJson) {
        log.info("loadTagButtonsFragment(tagsJson: {})", tagsJson);

        List<Tag> tags = new ArrayList<>();
        for (Map<String, Object> tagJson : tagsJson) {
            tags.add(tagService.parseTagJson(tagJson));
        }

        model.addAttribute(TAGS, tags);

        log.info("{}", model.asMap());
        return "fragments/tag::tag_buttons";
    }

    @PostMapping("/fragments/tag_badges")
    public String loadTagBadgesFragment(Model model, @RequestBody List<Map<String, Object>> tagsJson) {
        log.info("loadTagBadgesFragment(tagsJson: {})", tagsJson);

        List<Tag> tags = new ArrayList<>();
        for (Map<String, Object> tagJson : tagsJson) {
            tags.add(tagService.parseTagJson(tagJson));
        }

        model.addAttribute(TAGS, tags);

        log.info("{}", model.asMap());
        return "fragments/tag::tag_badges";
    }

}
