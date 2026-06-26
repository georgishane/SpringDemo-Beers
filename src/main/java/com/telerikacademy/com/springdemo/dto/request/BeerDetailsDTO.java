package com.telerikacademy.com.springdemo.dto.request;

import com.telerikacademy.com.springdemo.dto.response.CommentResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BeerDetailsDTO {
    private Long id;
    private String name;
    private String brewery;
    private String style;
    private Double abv;
    private Double averageRating;
    private Long ratingsCount;
    private List<CommentResponseDTO> comments;
}
