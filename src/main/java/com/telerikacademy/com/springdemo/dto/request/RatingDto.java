package com.telerikacademy.com.springdemo.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public class RatingDto {

    private int userId;

    @Min(1)
    @Max(5)
    @Positive(message = "Rating should be positive.")
    private double ratingScore;

    public RatingDto() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(double ratingScore) {
        this.ratingScore = ratingScore;
    }
}
