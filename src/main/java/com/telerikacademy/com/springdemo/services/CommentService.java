package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.models.Comment;
import com.telerikacademy.com.springdemo.dto.request.CommentRequestDTO;
import com.telerikacademy.com.springdemo.models.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {
    void deleteComment(Long commentId, User user);
    List<Comment> getCommentsByBeer(Long beerId);

    Comment createComment(User user, Long beerId, String text);

    Comment updateComment(Long id, CommentRequestDTO dto, String username);
    List<Comment> getCommentsByUser(String username);

    Page<Comment> searchComments(String text, String beerName, String userName,
                                 String fromDate, String toDate,
                                 String sortBy, String sortDir,
                                 int page, int size);

}
