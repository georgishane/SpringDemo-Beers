package com.telerikacademy.com.springdemo.controllers;

import com.telerikacademy.com.springdemo.exceptions.DuplicateEntityException;
import com.telerikacademy.com.springdemo.exceptions.DuplicateNonUnique;
import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.BeerDto;
import com.telerikacademy.com.springdemo.services.BeerService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/beers")
public class BeerController {

    private BeerService service;
    @Autowired
    public BeerController(BeerService service) {
            this.service = service;
    }

    @Transactional
    @GetMapping
    public List<Beer> getAll(
            @RequestParam(required = false) Optional<String> name
            , @RequestParam(required = false) Optional<Double> minAbv
            , @RequestParam(required = false) Optional<Double> maxAbv){


            return service.getAll().stream()
                    .filter(beer -> name.isEmpty() || beer.getName().contains(name.get()))
                    .filter(beer -> minAbv.isEmpty()|| beer.getAbv()>= minAbv.get())
                    .filter(beer -> maxAbv.isEmpty()|| beer.getAbv()<= maxAbv.get())
                    .collect(Collectors.toList());
    }

    @Transactional
    @GetMapping("/{id}")
    public Beer getBeerById(@PathVariable int id){
        try {
            return service.getById(id);
        } catch (EntityNotFoundException e) {
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND
                    ,e.getMessage());
        }

    }

    @GetMapping("/search")
    public Beer getBeerByName(@RequestParam String name){
        try {
            return service.getByName(name);
        } catch (EntityNotFoundException e) {
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND
                    ,e.getMessage());
        }

    }
    @Transactional
    @PostMapping
    public Beer createBeer(@Valid @RequestBody BeerDto beerDto){
        try {

            Beer beer = new Beer();
            beer.setName(beerDto.getName());
            beer.setAbv(beerDto.getAbv());
            service.createBeer(beer);
            return beer;

        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

    }
    @Transactional
    @PutMapping("/{id}")
    public Beer update(@PathVariable int id,  @Valid @RequestBody BeerDto beerDto){

        try {
            Beer beer = service.getById(id);
            beer.setName(beerDto.getName());
            beer.setAbv(beerDto.getAbv());

            service.updateBeer(beer);
            return beer;
        } catch (DuplicateNonUnique e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
       // catch (DuplicateEntityException e){
       //     throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
       // }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id){

    try {
        service.delete(id);

    }catch (EntityNotFoundException e){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }


    }
}
