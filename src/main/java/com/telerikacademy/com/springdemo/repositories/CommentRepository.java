package com.telerikacademy.com.springdemo.repositories;

import com.telerikacademy.com.springdemo.models.Comment;
import com.telerikacademy.com.springdemo.models.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    List<Comment> findAllByBeerId(Long beerId);
    List<Comment> findAllByUser_UserId(int userId);
    Comment findCommentById(Long id);
    List<Comment> findByUser(User user);
}
