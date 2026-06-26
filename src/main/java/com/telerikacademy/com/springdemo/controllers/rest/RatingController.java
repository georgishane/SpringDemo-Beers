package com.telerikacademy.com.springdemo.controllers.rest;

import com.telerikacademy.com.springdemo.dto.request.RatingDto;
import com.telerikacademy.com.springdemo.dto.response.RatingResponseDTO;
import com.telerikacademy.com.springdemo.models.*;
import com.telerikacademy.com.springdemo.security.AppUserDetails;
import com.telerikacademy.com.springdemo.services.BeerService;
import com.telerikacademy.com.springdemo.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;
    private final BeerService beerService;

    @PostMapping("/beer/{beerId}")
    public ResponseEntity<RatingResponseDTO> rateBeer(
            @PathVariable Long beerId,
            @RequestParam Double value,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        RatingResponseDTO response =
                ratingService.rateBeer(beerId, value, user);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/beer/{beerId}")
    public ResponseEntity<RatingResponseDTO> updateRating(
            @PathVariable Long beerId,
            @RequestParam Double value,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        RatingResponseDTO response =
                ratingService.updateRating(beerId, value, user);
       // ratingService.updateRating(beerId, value, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
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

        return ResponseEntity.ok(Map.of("myRating", rating.getRating(),"beerName", rating.getBeer().getName(), "beerId", rating.getBeer().getId(), "timestamp", rating.getTimestamp().toString(), "user", user.getUsername() + ""));
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
}
