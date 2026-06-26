package com.telerikacademy.com.springdemo.dto.request;

import com.telerikacademy.com.springdemo.models.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportCommentRequestDTO {
    private ReportStatus status;
    private String reason;
}
