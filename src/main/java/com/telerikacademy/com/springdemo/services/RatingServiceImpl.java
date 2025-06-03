package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.Rating;
import com.telerikacademy.com.springdemo.models.User;
import com.telerikacademy.com.springdemo.repositories.RatingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository repository;

    @Autowired
    public RatingServiceImpl(RatingRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Override
    public void rate(User user,Beer beer, double ratingValue){
        Rating existingRecord = repository.getByUserAndBeer(user, beer);

        if (existingRecord != null) {
            // Update the existing record
            existingRecord.setRating(ratingValue);
            repository.update(existingRecord);
        } else {
            // Create a new record
            var rating = new Rating(user, beer, ratingValue);
            repository.create(rating);
        }
    }

    @Transactional
    @Override
    public Double getAvgRating(Beer beer){

        var result = repository.getAverage(beer);
        if (result!=null){
            return result;
        }
        else {
            throw new EntityNotFoundException(beer.getName());
        }

    }
}
