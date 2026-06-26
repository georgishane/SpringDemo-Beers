package com.telerikacademy.com.springdemo.controllers.rest;

import com.telerikacademy.com.springdemo.controllers.AuthenticationHelper;
import com.telerikacademy.com.springdemo.dto.request.BeerDetailsDTO;
import com.telerikacademy.com.springdemo.dto.request.RatingDto;
import com.telerikacademy.com.springdemo.dto.response.BeerDto;
import com.telerikacademy.com.springdemo.dto.response.BeerResponseDTO;
import com.telerikacademy.com.springdemo.exceptions.DuplicateEntityException;
import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.exceptions.UnauthorizedOperationException;
import com.telerikacademy.com.springdemo.mappers.BeerMapper;
import com.telerikacademy.com.springdemo.models.*;
import com.telerikacademy.com.springdemo.security.AppUserDetails;
import com.telerikacademy.com.springdemo.mappers.ModelMapper;
import com.telerikacademy.com.springdemo.services.BeerService;

import com.telerikacademy.com.springdemo.services.RatingService;
import com.telerikacademy.com.springdemo.services.UserService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.telerikacademy.com.springdemo.exceptions.GlobalExceptionHandler.log;

@RestController
@RequestMapping("/api/beers")
public class BeerController {

    private final BeerService beerService;

    private final UserService userService;
    private final RatingService ratingService;
    private final ModelMapper modelMapper;
    private final BeerMapper beerMapper;

    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public BeerController(BeerService beerService, ModelMapper modelMapper, UserService userService, RatingService ratingService, BeerMapper beerMapper, AuthenticationHelper authenticationHelper) {
        this.beerService = beerService;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.ratingService = ratingService;
        this.beerMapper = beerMapper;
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
    public BeerDetailsDTO getBeerById(@PathVariable Long id) {
        try {
            return beerService.getBeerDetails(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND
                    , e.getMessage());
        }

    }
//
//    @GetMapping("/search")
//    public Beer getBeerByName(@RequestParam String name) {
//        try {
//            return beerService.getByName(name);
//        } catch (EntityNotFoundException e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND
//                    , e.getMessage());
//        }
//
//    }

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
    @GetMapping("/search")
    public Page<BeerResponseDTO> searchBeers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String style,
            @RequestParam(required = false) Double minAbv,
            @RequestParam(required = false) Double maxAbv,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Beer> beers = beerService.searchBeers(
                name, style, minAbv, maxAbv, sortBy, direction, page, size
        );
        return beers.map(beer -> {
            BeerResponseDTO dto = beerMapper.toBeerResponseDTO(beer);

            // safe enrichment of average rating
            try {
                Long beerId = Long.valueOf(beer.getId());
                Double avg = ratingService.getAvgRatingByBeerId(beerId);
                if (avg != null) {
                    avg = BigDecimal.valueOf(avg)
                            .setScale(2, RoundingMode.HALF_UP)
                            .doubleValue();
                }
                dto.setAverageRating(avg);

            } catch (Exception ex) {
                // log and default so mapping never throws

                log.error("Failed to fetch avg rating for beer id {}: {}", beer.getId(), ex.getMessage(), ex);
                dto.setAverageRating(0.0);
            }

            return dto;
        });
    }
}
