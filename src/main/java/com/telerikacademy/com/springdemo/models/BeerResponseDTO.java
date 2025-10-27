package com.telerikacademy.com.springdemo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BeerResponseDTO {
    Long id;
    String name;
    String brewery;
    Style style;
    double abv;
    Double AverageRating;
    String sortBy;
    String direction;
}
