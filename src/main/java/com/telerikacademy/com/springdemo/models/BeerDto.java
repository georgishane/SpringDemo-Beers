package com.telerikacademy.com.springdemo.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class BeerDto {

    @NotNull(message = "Name can't be empty")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 symbols")
    private String name;

    @Positive(message = "Abv should be positive")
    private double abv;

    public BeerDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAbv() {
        return abv;
    }

    public void setAbv(double abv) {
        this.abv = abv;
    }
}
