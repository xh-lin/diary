package com.xuhuang.diary.services;

import java.util.List;

import javax.security.auth.message.AuthException;

import com.xuhuang.diary.models.Book;
import com.xuhuang.diary.models.Record;
import com.xuhuang.diary.models.User;
import com.xuhuang.diary.repositories.BookRepository;
import com.xuhuang.diary.repositories.RecordRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryService {

    public static final String TITLE_MUST_NOT_BE_BLANK = "Title must not be blank.";
    public static final String YOU_DO_NOT_HAVE_PERMISSION_TO_ACCESS = "You do not have permission to access.";
    public static final String PAGE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_ZERO = "Page must be greater than or equal to zero.";
    public static final String SIZE_MUST_BE_GREATER_THAN_ZERO = "Size must be greater than zero.";

    private final BookRepository bookRepository;
    private final RecordRepository recordRepository;
    private final UserService userService;

    /*
        Throws:
            IllegalArgumentException - if title is blank
    */
    public Book createBook(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(TITLE_MUST_NOT_BE_BLANK);
        }
        return bookRepository.save(new Book(title, userService.getCurrentUser(), null));
    }

    public List<Book> getBooks() {
        return bookRepository.findByUser(userService.getCurrentUser());
    }

    /*
        Throws:
            AuthException - if book does not belong to the current user
            NoSuchElementException - if book is not found
    */
    public Book getBook(Long bookId) throws AuthException {
        Book book = bookRepository.findById(bookId).orElseThrow();
        throwIfIsNotCurrentUser(book.getUser());
        return book;
    }

    /*
        Throws:
            AuthException - if book does not belong to the current user
            NoSuchElementException - if book is not found
            IllegalArgumentException - if title is blank
    */
    public Book updateBook(Long bookId, String title) throws AuthException {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(TITLE_MUST_NOT_BE_BLANK);
        }
        Book book = bookRepository.findById(bookId).orElseThrow();
        throwIfIsNotCurrentUser(book.getUser());
        book.setTitle(title);
        return bookRepository.save(book);
    }

    /*
        Throws:
            AuthException - if book does not belong to the current user
            NoSuchElementException - if book is not found
    */
    public Book deleteBook(Long bookId) throws AuthException {
        Book book = bookRepository.findById(bookId).orElseThrow();
        throwIfIsNotCurrentUser(book.getUser());
        bookRepository.deleteById(bookId);
        return book;
    }

    /*
        Throws:
            AuthException - if book does not belong to the current user
            NoSuchElementException - if book is not found
    */
    public Record createRecord(Long bookId, String text) throws AuthException {
        Book book = bookRepository.findById(bookId).orElseThrow();
        throwIfIsNotCurrentUser(book.getUser());
        return recordRepository.save(new Record(text, book));
    }

    /*
        Throws:
            AuthException - if book does not belong to the current user
            NoSuchElementException - if book is not found
            IllegalArgumentException - if either page < 0 or size <= 0
    */
    public Page<Record> getRecords(Long bookId, int page, int size) throws AuthException {
        if (page < 0) {
            throw new IllegalArgumentException(PAGE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_ZERO);
        } else if (size <= 0) {
            throw new IllegalArgumentException(SIZE_MUST_BE_GREATER_THAN_ZERO);
        }

        Book book = bookRepository.findById(bookId).orElseThrow();
        throwIfIsNotCurrentUser(book.getUser());
        Pageable pageable = PageRequest.of(page, size);
        return recordRepository.findByBook(book, pageable);
    }

    /*
        Throws:
            AuthException - if book does not belong to the current user
            NoSuchElementException - if book is not found
    */
    public Record getRecord(Long recordId) throws AuthException {
        Record recd = recordRepository.findById(recordId).orElseThrow();
        throwIfIsNotCurrentUser(recd.getBook().getUser());
        return recd;
    }

    /*
        Throws:
            AuthException - if book does not belong to the current user
            NoSuchElementException - if book is not found
    */
    public Record updateRecord(Long recordId, String text) throws AuthException {
        Record recd = recordRepository.findById(recordId).orElseThrow();
        throwIfIsNotCurrentUser(recd.getBook().getUser());
        recd.setText(text);
        return recordRepository.save(recd);
    }

    /*
        Throws:
            AuthException - if book does not belong to the current user
            NoSuchElementException - if book is not found
    */
    public Record deleteRecord(Long recordId) throws AuthException {
        Record recd = recordRepository.findById(recordId).orElseThrow();
        throwIfIsNotCurrentUser(recd.getBook().getUser());
        recordRepository.deleteById(recordId);
        return recd;
    }

    private void throwIfIsNotCurrentUser(User user) throws AuthException {
        if (!userService.isCurrentUser(user)) {
            throw new AuthException(YOU_DO_NOT_HAVE_PERMISSION_TO_ACCESS);
        }
    }

}
