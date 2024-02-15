package com.telerikacademy.com.springdemo.repositories;

import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.models.Beer;

import java.util.*;

public class BeerMapRepositoryImpl implements BeerRepository{

    private Map<Integer, Beer> beers;

    public BeerMapRepositoryImpl (){

        this.beers = new HashMap<>();
    }
    @Override
    public List<Beer> getAll() {
        return new ArrayList<>(beers.values());
    }

    @Override
    public List<Beer> filter(Optional<String> name, Optional<Double> MinAbv) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Beer getById(int id) {
        Beer beer = beers.get(id);
        if (beer == null){
            throw new EntityNotFoundException("Beer", id);
        }
        return beer;
    }

    @Override
    public Beer getByName(String name) {
        return beers.values().stream()
                .filter(beer -> beer.getName().equals(name))
                .findFirst()
                .orElseThrow(()-> new EntityNotFoundException("Beer", "name", name));
    }

    @Override
    public void create(Beer beer) {
        beers.put(beer.getId(), beer);
    }

    @Override
    public void update(Beer beer) {
    beers.replace(beer.getId(), beer);
    }

    @Override
    public void delete(int id) {
        beers.remove(id);
    }
}
