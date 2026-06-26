package com.telerikacademy.com.springdemo.dto.response;

import com.telerikacademy.com.springdemo.models.ReportStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportCommentResponseDTO {
    private Long id;
    private Long commentId;
    private String reportedBy;
    private String reason;
    private LocalDateTime reportedAt;
    @Enumerated(EnumType.STRING)
    private ReportStatus status;
    private String text;
}

