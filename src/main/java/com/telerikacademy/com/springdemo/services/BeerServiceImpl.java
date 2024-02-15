package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.exceptions.DuplicateEntityException;
import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.repositories.BeerRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.NonUniqueResultException;

import java.util.List;


@Service
public class BeerServiceImpl implements BeerService {

    private BeerRepository repository;
    @Autowired
    public BeerServiceImpl(BeerRepository repository){
        this.repository = repository;
    }

    @Transactional
    @Override
    public List<Beer> getAll(){
        return repository.getAll();
    }

    @Transactional
    @Override
    public Beer getById(int id){

        return repository.getById(id);

    }

    @Transactional
    @Override
    public Beer getByName(String name){

        return repository.getByName(name);
    }

    @Transactional
    @Override
    public void createBeer(Beer beer){
        boolean duplicateExists = true;

        try {
            repository.getByName(beer.getName());

        }catch (EntityNotFoundException e){
            duplicateExists = false;
        }

        if (duplicateExists){
            throw new DuplicateEntityException("Beer", "name", beer.getName());
        }
        repository.create(beer);
    }


    @Transactional
    @Override
    public void updateBeer(Beer beer){
        boolean duplicateExists = true;

        try {
            Beer existingBeer = repository.getByName(beer.getName());
            if (existingBeer.getId() == beer.getId()){
                duplicateExists = false;
            }


        }catch (NonUniqueResultException e){
            duplicateExists = false;
        }


        if (duplicateExists){
            throw new DuplicateEntityException("Beer", "name", beer.getName());
        }

        repository.update(beer);
    }
    @Override
    public void delete(int id){
        repository.delete(id);

    }
}
