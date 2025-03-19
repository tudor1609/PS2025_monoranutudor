package com.example.demo.repository;

import com.example.demo.entity.Post;
import com.example.demo.entity.Hashtag;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthor(User author);

    List<Post> findByTextContainingIgnoreCase(String text);

    @Query("SELECT p FROM Post p JOIN p.hashtags h WHERE h.name = :name")
    List<Post> findByHashtagName(String name);
}
