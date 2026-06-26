package com.telerikacademy.com.springdemo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)

public class BeerResponseDTO {
    Long id;
    String name;
    String brewery;
    String style;
    double abv;
    Double AverageRating;
    String sortBy;
    String direction;
    Long ratingsCount;
    List<CommentResponseDTO> comments;
}
