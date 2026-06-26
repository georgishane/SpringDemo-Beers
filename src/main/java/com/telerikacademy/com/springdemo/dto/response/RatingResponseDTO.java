package com.telerikacademy.com.springdemo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingResponseDTO {
    private Long beerId;
    private Double rating;
    private Double averageRating;
}
