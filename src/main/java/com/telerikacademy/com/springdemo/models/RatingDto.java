package com.telerikacademy.com.springdemo.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public class RatingDto {

    private int userId;

    @Min(0)
    @Max(5)
    @Positive(message = "Rating should be positive.")
    private double rating;

    public RatingDto() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
