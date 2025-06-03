package com.telerikacademy.com.springdemo.repositories;

import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.Rating;
import com.telerikacademy.com.springdemo.models.User;

public interface RatingRepository {

    void update(Rating rating);

    void create(Rating rating);

    Rating getByUserAndBeer(User user, Beer beer);

    boolean ratingExistsForUserAndBeer(User user, Beer beer);

    Double getAverage(Beer beer);
}
