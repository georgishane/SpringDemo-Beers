package com.telerikacademy.com.springdemo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponseDTO {
    private Long id;
    private String userName;
    private String beerName;
    private String text;
    private LocalDateTime createdAt;
}
