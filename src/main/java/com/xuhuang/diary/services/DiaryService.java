package com.xuhuang.diary.services;

import java.util.List;

import javax.security.auth.message.AuthException;

import com.xuhuang.diary.models.Book;
import com.xuhuang.diary.repositories.BookRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryService {

    public static final String TITLE_MUST_NOT_BE_BLANK = "Title must not be blank.";
    public static final String NOT_AUTHORIZED = "Not authorized.";
    
    private final BookRepository bookRepository;
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
        if (!userService.isCurrentUser(book.getUser())) {
            throw new AuthException(NOT_AUTHORIZED);
        }
        return book;
    }

    /*
        Throws:
            AuthException - if book does not belong to the current user
            NoSuchElementException - if book is not found
    */
    public Book updateBook(Long bookId, String title) throws AuthException {
        Book book = bookRepository.findById(bookId).orElseThrow();
        if (!userService.isCurrentUser(book.getUser())) {
            throw new AuthException(NOT_AUTHORIZED);
        }
        book.setTitle(title);
        return bookRepository.save(book);
    }

    /*
        Throws:
            AuthException - if book does not belong to current user
            NoSuchElementException - if book with id is not found
    */
    public Book deleteBook(Long bookId) throws AuthException {
        Book book = bookRepository.findById(bookId).orElseThrow();
        if (!userService.isCurrentUser(book.getUser())) {
            throw new AuthException(NOT_AUTHORIZED);
        }
        bookRepository.deleteById(bookId);
        return book;
    }
    
}
