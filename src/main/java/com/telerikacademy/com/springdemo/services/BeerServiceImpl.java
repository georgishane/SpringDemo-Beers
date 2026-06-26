package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.exceptions.DuplicateEntityException;
import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.exceptions.UnauthorizedOperationException;
import com.telerikacademy.com.springdemo.mappers.BeerMapper;
import com.telerikacademy.com.springdemo.mappers.ModelMapper;
import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.dto.request.BeerDetailsDTO;

import com.telerikacademy.com.springdemo.models.User;
import com.telerikacademy.com.springdemo.repositories.BeerRepository;
import com.telerikacademy.com.springdemo.repositories.CommentRepository;
import com.telerikacademy.com.springdemo.repositories.RatingRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


@Service
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;
    private final RatingService ratingService;
    private final RatingRepository ratingRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public BeerServiceImpl(BeerRepository repository, BeerMapper beerMapper, RatingService ratingService, RatingRepository ratingRepository, CommentRepository commentRepository, ModelMapper modelMapper) {
        this.beerRepository = repository;
        this.beerMapper = beerMapper;
        this.ratingService = ratingService;
        this.ratingRepository = ratingRepository;
        this.commentRepository = commentRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    @Override
    public List<Beer> getAll() {
        return beerRepository.findAll();
    }

    @Transactional
    @Override
    public Beer getById(Long id) {

        return beerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Beer not found with id: " + id));

    }

    @Transactional
    @Override
    public Beer getByName(String name) {

        return beerRepository.getByName(name);
    }
    @Override
    public BeerDetailsDTO getBeerDetails(Long id) {
        Beer beer = beerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Beer not found"));
        BeerDetailsDTO  dto = beerMapper.toDetailsDto(beer);

        Double avg = ratingService.getAvgRatingByBeerId(beer.getId());
        if (avg != null) {
            avg = BigDecimal.valueOf(avg)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }
        dto.setAverageRating(avg);
        long ratingsCount = ratingRepository.countByBeerId(id);
        dto.setRatingsCount(ratingsCount);
        dto.setComments(
                commentRepository.findByBeerIdOrderByCreatedAtDesc(id)
                        .stream()
                        .map(modelMapper::toDto)
                        .toList()
        );
        return dto;
    }

    @Transactional
    @Override
    public void createBeer(Beer beer) {
        boolean duplicateExists = true;

        try {
            beerRepository.getByName(beer.getName());

        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new DuplicateEntityException("Beer", "name", beer.getName());
        }
        beerRepository.save(beer);
    }


    @Transactional
    @Override
    public void updateBeer(Beer beer, User user) {
        verifyCanModify(beer, user);

        boolean duplicateExists = true;

        try {
            Beer existingBeer = beerRepository.getByName(beer.getName());
            if (existingBeer.getId() == beer.getId()) {
                duplicateExists = false;
            }
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new DuplicateEntityException("Beer", "name", beer.getName());
        }

        beerRepository.save(beer);
    }

    @Transactional
    @Override
    public void delete(Long id, User user) {
        Beer beer = beerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Beer not found with id: " + id));

        if (!beer.getCreatedBy().equals(user) || !user.hasRole("ADMIN")) {
            throw new UnauthorizedOperationException("Only creator or admin can modify a beer");
        }
        beerRepository.deleteById(id);

    }


    @Override
    public Page<Beer> searchBeers(String name, String style, Double minAbv, Double maxAbv, String sortBy, String direction, int page, int size) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);
        return beerRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (style != null && !style.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("style").get("name")), style.toLowerCase()));
            }
            if (minAbv != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("abv"), minAbv));
            }
            if (maxAbv != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("abv"), maxAbv));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);

    }
    private void verifyCanModify(Beer beer, User user) {
        if (!beer.getCreatedBy().equals(user) && !user.hasRole("ADMIN")) {
            throw new UnauthorizedOperationException("Only creator and admin can modify a beer");
        }
    }

}
