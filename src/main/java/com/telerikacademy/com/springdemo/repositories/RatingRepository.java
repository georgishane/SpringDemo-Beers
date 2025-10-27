package com.telerikacademy.com.springdemo.repositories;

import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.Rating;
import com.telerikacademy.com.springdemo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByUserAndBeer(User user, Beer beer);

    boolean existsByUserAndBeer(User user, Beer beer);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.beer = :beer")
    Double getAverage(@Param("beer") Beer beer);

    List<Rating> findAllByBeerOrderByTimestampDesc(Beer beer);
    List<Rating> findAllByUser(User user);

}
