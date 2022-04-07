package com.xuhuang.diary.repositories;

import java.util.List;

import com.xuhuang.diary.models.Book;
import com.xuhuang.diary.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    List<Book> findByUser(User user);

}
