package com.xuhuang.diary.repositories;

import java.util.List;

import com.xuhuang.diary.models.Tag;
import com.xuhuang.diary.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByUser(User user);

}
