package com.telerikacademy.com.springdemo.controllers.rest;

import com.telerikacademy.com.springdemo.controllers.AuthenticationHelper;
import com.telerikacademy.com.springdemo.exceptions.DuplicateEntityException;
import com.telerikacademy.com.springdemo.exceptions.DuplicateNonUnique;
import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.exceptions.UnauthorizedOperationException;
import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.BeerDto;
import com.telerikacademy.com.springdemo.models.RatingDto;
import com.telerikacademy.com.springdemo.models.User;
import com.telerikacademy.com.springdemo.services.ModelMapper;
import com.telerikacademy.com.springdemo.services.BeerService;

import com.telerikacademy.com.springdemo.services.RatingService;
import com.telerikacademy.com.springdemo.services.UserService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/beers")
public class BeerController {

    private final BeerService beerService;

    private final UserService userService;
    private final RatingService ratingService;
    private final ModelMapper modelMapper;

    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public BeerController(BeerService beerService, ModelMapper modelMapper, UserService userService, RatingService ratingService, AuthenticationHelper authenticationHelper) {
        this.beerService = beerService;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.ratingService = ratingService;
        this.authenticationHelper = authenticationHelper;
    }


    @GetMapping
    public List<Beer> getAll(
            @RequestParam(required = false) Optional<String> name
            , @RequestParam(required = false) Optional<Double> minAbv
            , @RequestParam(required = false) Optional<Double> maxAbv) {


        return beerService.getAll().stream()
                .filter(beer -> name.isEmpty() || beer.getName().contains(name.get()))
                .filter(beer -> minAbv.isEmpty() || beer.getAbv() >= minAbv.get())
                .filter(beer -> maxAbv.isEmpty() || beer.getAbv() <= maxAbv.get())
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public Beer getBeerById(@PathVariable int id) {
        try {
            return beerService.getById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND
                    , e.getMessage());
        }

    }

    @GetMapping("/search")
    public Beer getBeerByName(@RequestParam String name) {
        try {
            return beerService.getByName(name);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND
                    , e.getMessage());
        }

    }

    @PostMapping
    public Beer createBeer(@RequestHeader HttpHeaders headers, @Valid @RequestBody BeerDto beerDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Beer beer = modelMapper.fromDto(beerDto);
            beer.setCreatedBy(user);
            beerService.createBeer(beer);
            return beer;

        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

    }

    @PostMapping("/{id}/rating")
    public void rate(@PathVariable int id, @RequestBody RatingDto ratingDto) {
        var beer = getBeerById(id);
        User user;
        try {

            user = userService.getById(ratingDto.getUserId());


        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

        ratingService.rate(user, beer, ratingDto.getRating());

    }

    @GetMapping("/{id}/rating")
    public Double rate(@PathVariable int id) {
        var beer = getBeerById(id);

        return ratingService.getAvgRating(beer);
    }

    @PutMapping("/{id}")
    public Beer update(@RequestHeader HttpHeaders headers, @PathVariable int id, @Valid @RequestBody BeerDto beerDto) {

        try {
            User user = authenticationHelper.tryGetUser(headers);
            Beer beer = modelMapper.fromDto(beerDto, id);
            beer.setCreatedBy(user);
            beerService.updateBeer(beer, user);
            return beer;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader HttpHeaders headers, @PathVariable int id) {

        try {
            User user = authenticationHelper.tryGetUser(headers);

            beerService.delete(id, user);

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }

    }
}
