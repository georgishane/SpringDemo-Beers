package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.BeerDto;
import com.telerikacademy.com.springdemo.models.BeerResponseDTO;
import com.telerikacademy.com.springdemo.models.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BeerService {
    List<Beer> getAll();

    @Transactional
    Beer getById(Long id);

    Beer getByName (String name);

    void createBeer(Beer beer);

    void updateBeer(Beer beer, User user);

    @Transactional
    void delete(Long id, User user);

    public List<BeerResponseDTO> searchBeers(
            String name,
            String type,
            Double minAbv,
            Double maxAbv,
            String sortBy,
            String direction
    );

}
