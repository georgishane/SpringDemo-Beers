package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.Rating;
import com.telerikacademy.com.springdemo.models.User;

import java.util.List;

public interface RatingService {

    Double getAvgRating(Beer beer);

    List<Rating> getAllRatingsForBeer(Beer beer);

    Rating getUserRatingForBeer(User user, Long beerId);

    Rating rateBeer(User user, Beer beer, double rate);

    void save(Rating rating);
    void delete(Rating rating);

    List<Rating> getAllRatingsByUser(User user);
}
