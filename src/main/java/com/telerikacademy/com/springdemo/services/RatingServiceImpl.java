package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.Rating;
import com.telerikacademy.com.springdemo.models.User;
import com.telerikacademy.com.springdemo.repositories.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository repository;
    private final BeerService beerService;
    @Autowired
    public RatingServiceImpl(RatingRepository repository, BeerService beerService) {
        this.repository = repository;
        this.beerService = beerService;
    }

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
    @Override
    public List<Rating> getAllRatingsForBeer(Beer beer) {
        return repository.findAllByBeerOrderByTimestampDesc(beer);
    }

    @Override
    public Rating getUserRatingForBeer(User user, Long beerId) {
        Beer beer = beerService.getById(beerId); // Or directly with Long if updated
        return repository.findByUserAndBeer(user, beer).orElse(null);
    }
    @Transactional
    @Override
    public Rating rateBeer(User user, Beer beer, double rate) {
        Optional<Rating> optionalRating = repository.findByUserAndBeer(user, beer);

        Rating rating = optionalRating.orElseGet(() -> {
            Rating newRating = new Rating();
            newRating.setUser(user);
            newRating.setBeer(beer);
            return newRating;
        });

        rating.setRating(rate);
        rating.setTimestamp(LocalDateTime.now());

        return repository.save(rating);
    }
    @Transactional
    @Override
    public void save(Rating rating) {
        repository.save(rating);
    }
    @Transactional
    @Override
    public void delete(Rating rating) {
        repository.delete(rating);
    }
    @Override
    public List<Rating> getAllRatingsByUser(User user) {
        return repository.findAllByUser(user);
    }
}
