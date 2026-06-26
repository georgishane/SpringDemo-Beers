package com.telerikacademy.com.springdemo.dto.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BeerDto {

    @NotNull(message = "Name can't be empty")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 symbols")
    private String name;

    @Positive(message = "Abv should be positive")
    private double abv;

    @Positive(message = "StyleId should be positive")
    private int styleId;

    public BeerDto() {
    }

}
