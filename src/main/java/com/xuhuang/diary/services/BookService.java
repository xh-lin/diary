package com.xuhuang.diary.services;

import static com.xuhuang.diary.utils.Utils.asTimestamp;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.security.auth.message.AuthException;

import com.xuhuang.diary.models.Book;
import com.xuhuang.diary.repositories.BookRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService extends BaseService {

    public static final String TITLE_MUST_NOT_BE_BLANK = "Title must not be blank.";

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;

    private final BookRepository bookRepository;
    private final UserService userService;

    /*
     * Throws:
     * IllegalArgumentException - if title is blank
     */
    public Book createBook(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(TITLE_MUST_NOT_BE_BLANK);
        }
        return bookRepository.save(new Book(title.trim(), userService.getCurrentUser()));
    }

    public List<Book> getBooks() {
        return bookRepository.findByUser(userService.getCurrentUser());
    }

    /*
     * Throws:
     * AuthException - if book does not belong to the current user
     * NoSuchElementException - if book is not found
     */
    public Book getBook(Long bookId) throws AuthException {
        Book book = bookRepository.findById(bookId).orElseThrow();
        throwIfIsNotCurrentUser(book.getUser());
        return book;
    }

    /*
     * Throws:
     * AuthException - if book does not belong to the current user
     * NoSuchElementException - if book is not found
     * IllegalArgumentException - if title is blank
     */
    public Book updateBook(Long bookId, String title) throws AuthException {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(TITLE_MUST_NOT_BE_BLANK);
        }
        Book book = bookRepository.findById(bookId).orElseThrow();
        throwIfIsNotCurrentUser(book.getUser());
        book.setTitle(title.trim());
        return bookRepository.save(book);
    }

    /*
     * Throws:
     * AuthException - if book does not belong to the current user
     * NoSuchElementException - if book is not found
     */
    public Book deleteBook(Long bookId) throws AuthException {
        Book book = bookRepository.findById(bookId).orElseThrow();
        throwIfIsNotCurrentUser(book.getUser());
        bookRepository.deleteById(bookId);
        return book;
    }

    public Book parseBookJson(Map<String, Object> bookJson, boolean getRelated) {
        Long id = Long.valueOf(((Integer) bookJson.get("id")).longValue());

        if (getRelated) {
            try {
                return getBook(id);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        Timestamp createdAt = asTimestamp((String) bookJson.get("createdAt"));
        Timestamp updateddAt = asTimestamp((String) bookJson.get("updatedAt"));
        String title = (String) bookJson.get("title");
        Book book = new Book();

        book.setId(id);
        book.setCreatedAt(createdAt);
        book.setUpdatedAt(updateddAt);
        book.setTitle(title);
        return book;
    }

    public Book parseBookJson(Map<String, Object> bookJson) {
        return parseBookJson(bookJson, false);
    }

}
