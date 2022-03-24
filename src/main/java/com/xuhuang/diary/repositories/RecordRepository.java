package com.xuhuang.diary.repositories;

import java.util.List;

import com.xuhuang.diary.models.Book;
import com.xuhuang.diary.models.Record;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    
    List<Record> findByBook(Book book);

    Page<Record> findByBook(Book book, Pageable pageable);

}