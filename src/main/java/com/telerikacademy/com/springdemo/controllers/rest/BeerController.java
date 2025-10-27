package com.telerikacademy.com.springdemo.controllers.rest;

import com.telerikacademy.com.springdemo.controllers.AuthenticationHelper;
import com.telerikacademy.com.springdemo.exceptions.DuplicateEntityException;
import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.exceptions.UnauthorizedOperationException;
import com.telerikacademy.com.springdemo.models.*;
import com.telerikacademy.com.springdemo.security.AppUserDetails;
import com.telerikacademy.com.springdemo.services.ModelMapper;
import com.telerikacademy.com.springdemo.services.BeerService;

import com.telerikacademy.com.springdemo.services.RatingService;
import com.telerikacademy.com.springdemo.services.UserService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @GetMapping("{beerId}/rating/average")
    public ResponseEntity<?> getAverageRating(@PathVariable Long beerId){

        Beer beer = beerService.getById(beerId);
        Double average = ratingService.getAvgRating(beer);

        if (average == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No ratings found for this beer."));
        }

        return ResponseEntity.ok(Map.of("averageRating", average));
    }

    @GetMapping("/{id}")
    public Beer getBeerById(@PathVariable Long id) {
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

//    @PostMapping
//    public Beer createBeer(@RequestHeader HttpHeaders headers, @Valid @RequestBody BeerDto beerDto) {
//        try {
//            User user = authenticationHelper.tryGetUser(headers);
//            Beer beer = modelMapper.fromDto(beerDto);
//            beer.setCreatedBy(user);
//            beerService.createBeer(beer);
//            return beer;
//
//        } catch (DuplicateEntityException e) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
//        }
//
//    }

    @PostMapping("/{beerId}/rate")
    public ResponseEntity<?> rateBeer(
            @PathVariable Long beerId,
            @RequestBody RatingDto ratingDto,
            Authentication authentication) {

        // Get user
        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // Get beer
        Beer beer = beerService.getById(beerId);

        // Save or update rating
        ratingService.rateBeer(user, beer, ratingDto.getRatingScore());

        return ResponseEntity.ok(Map.of("message", "Beer rated successfully"));
    }
    @PutMapping("/{beerId}/rate")
    public ResponseEntity<?> updateRating(@PathVariable Long beerId,
                                          @RequestBody RatingDto ratingDto,
                                          Authentication authentication) {
        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        Rating rating = ratingService.getUserRatingForBeer(user, beerId);

        if (rating == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No rating found to update."));
        }

        rating.setRating(ratingDto.getRatingScore());
        rating.setTimestamp(LocalDateTime.now());
        ratingService.save(rating);

        return ResponseEntity.ok(Map.of("message", "Rating updated successfully"));
    }

    @DeleteMapping("/{beerId}/rate")
    public ResponseEntity<?> deleteRating(@PathVariable Long beerId,
                                          Authentication authentication) {
        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        Rating rating = ratingService.getUserRatingForBeer(user, beerId);

        if (rating == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No rating found to delete."));
        }

        ratingService.delete(rating);
        return ResponseEntity.ok(Map.of("message", "Rating deleted successfully"));
    }


    @GetMapping("/{beerId}/rating/me")
    public ResponseEntity<?> getMyRating(@PathVariable Long beerId, Authentication authentication) {
        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        Rating rating = ratingService.
                getUserRatingForBeer(user, beerId);

        if (rating == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "You haven't rated this beer yet."));
        }

        return ResponseEntity.ok(Map.of("myRating", rating.getRating()));
    }
    @GetMapping("/{beerId}/ratings")
    public ResponseEntity<?> getAllRatings(@PathVariable Long beerId) {
        Beer beer = beerService.getById(beerId);
        List<Rating> ratings = ratingService.getAllRatingsForBeer(beer);

        List<Map<String, Object>> response = ratings.stream().map(rating -> {
            Map<String, Object> map = new HashMap<>();
            map.put("user", rating.getUser().getUsername());
            map.put("rating", rating.getRating());
            map.put("timestamp", rating.getTimestamp());
            map.put("beerName", rating.getBeer().getName());
            return map;
        }).toList();

        return ResponseEntity.ok(response);
    }
    @GetMapping("/ratings/me")
    public ResponseEntity<?> getMyRatings(Authentication authentication) {
        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        List<Rating> ratings = ratingService.getAllRatingsByUser(user);


        List<Map<String, Object>> response = ratings.stream().map(rating -> {
            Map<String, Object> map = new HashMap<>();
            map.put("beerId", rating.getBeer().getId());
            map.put("beerName", rating.getBeer().getName());
            map.put("rating", rating.getRating());
            map.put("timestamp", rating.getTimestamp());
            return map;
        }).toList();
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public Beer update(@RequestHeader HttpHeaders headers, @PathVariable Long id, @Valid @RequestBody BeerDto beerDto) {

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
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@RequestHeader HttpHeaders headers, @PathVariable Long id) {

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
