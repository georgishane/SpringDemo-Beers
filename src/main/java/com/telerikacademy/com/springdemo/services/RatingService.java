package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.User;

public interface RatingService {
    void rate(User user, Beer beer, double rating);

    Double getAvgRating(Beer beer);
}
