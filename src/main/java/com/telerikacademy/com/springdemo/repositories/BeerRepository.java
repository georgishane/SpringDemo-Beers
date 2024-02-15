package com.telerikacademy.com.springdemo.repositories;
import com.telerikacademy.com.springdemo.models.Beer;

import java.util.List;
import java.util.Optional;

public interface BeerRepository extends Repository<Beer> {

    List<Beer> filter(Optional<String> name, Optional<Double> MinAbv);

    Beer getByName(String name);

}
