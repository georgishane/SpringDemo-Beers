package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.models.*;
import com.telerikacademy.com.springdemo.repositories.CommentReportRepository;
import com.telerikacademy.com.springdemo.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentReportService {
    private final CommentRepository commentRepository;
    private final CommentReportRepository reportRepository;

    public CommentReport reportComment(Long commentId, User reporter, String reason) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (reportRepository.existsByCommentAndReportedBy(comment, reporter)) {
            throw new IllegalStateException("You have already reported this comment");
        }

        CommentReport report = new CommentReport();
        report.setComment(comment);
        report.setReportedBy(reporter);
        report.setReason(reason);
        report.setReportedAt(LocalDateTime.now());

        return reportRepository.save(report);
    }
    public List<ReportCommentResponseDTO> getAllReports() {
        return reportRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }
    public ReportCommentResponseDTO dismissReport(Long reportId, ReportStatus reportStatus) {
        CommentReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        report.setStatus(reportStatus);
        reportRepository.save(report);

        return mapToDTO(report);
    }

    public ReportCommentResponseDTO resolveReport(Long reportId) {
        CommentReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        Comment comment = report.getComment();
        if (comment != null) {
            // delete the comment
            commentRepository.delete(comment);

            // mark ALL reports for this comment as RESOLVED
            reportRepository.updateStatusByCommentId(comment.getId(), "RESOLVED");
        }

        return mapToDTO(report);
    }
    private ReportCommentResponseDTO mapToDTO(CommentReport report) {
        return ReportCommentResponseDTO.builder()
                .id(report.getId())
                .commentId(report.getComment().getId())
                .reportedBy(report.getReportedBy().getUsername())
                .reason(report.getReason())
                .reportedAt(report.getReportedAt())
                .status(report.getStatus())
                .text(reportRepository.findCommentTextByReportId(report.getId()))
                .build();
    }
    public ReportCommentResponseDTO moderateReportedComment(Long reportId, ReportStatus newStatus) {
        CommentReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));
       // report.setText(reportRepository.findCommentText(report.getComment().getId()));
        report.setStatus(newStatus);
        reportRepository.save(report);
        return mapToDTO(report);

    }

}
