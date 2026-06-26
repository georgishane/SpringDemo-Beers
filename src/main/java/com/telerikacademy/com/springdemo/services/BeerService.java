package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.dto.request.BeerDetailsDTO;
import com.telerikacademy.com.springdemo.models.*;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BeerService {
    List<Beer> getAll();

    @Transactional
    Beer getById(Long id);

    Beer getByName (String name);

    BeerDetailsDTO getBeerDetails(Long id);
    void createBeer(Beer beer);

    void updateBeer(Beer beer, User user);

    @Transactional
    void delete(Long id, User user);

    Page<Beer> searchBeers(String name, String style, Double minAbv, Double maxAbv, String sortBy, String direction, int page, int size);
}
