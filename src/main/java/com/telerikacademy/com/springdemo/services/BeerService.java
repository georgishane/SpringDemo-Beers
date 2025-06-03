package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.User;

import java.util.List;

public interface BeerService {
    List<Beer> getAll();

    Beer getById(int id);

    Beer getByName (String name);

    void createBeer(Beer beer);

    void updateBeer(Beer beer, User user);

    void delete(int id, User user);
}
