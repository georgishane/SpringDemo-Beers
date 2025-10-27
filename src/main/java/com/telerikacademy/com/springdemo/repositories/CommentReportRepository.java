package com.telerikacademy.com.springdemo.repositories;

import com.telerikacademy.com.springdemo.models.Comment;
import com.telerikacademy.com.springdemo.models.CommentReport;
import com.telerikacademy.com.springdemo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
    boolean existsByCommentAndReportedBy(Comment comment, User reportedBy);
    List<CommentReport> findByComment(Comment comment);
    @Modifying
    @Query("UPDATE CommentReport r SET r.status = :status WHERE r.comment.id = :commentId")
    void updateStatusByCommentId(@Param("commentId") Long commentId, @Param("status") String status);

    @Query("SELECT cr.comment.text FROM CommentReport cr WHERE cr.id = :reportId")
    String findCommentTextByReportId(Long reportId);
}
