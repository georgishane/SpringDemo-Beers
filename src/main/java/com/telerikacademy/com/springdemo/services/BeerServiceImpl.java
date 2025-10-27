package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.exceptions.DuplicateEntityException;
import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.exceptions.UnauthorizedOperationException;
import com.telerikacademy.com.springdemo.mappers.BeerMapper;
import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.BeerDto;
import com.telerikacademy.com.springdemo.models.BeerResponseDTO;
import com.telerikacademy.com.springdemo.models.User;
import com.telerikacademy.com.springdemo.repositories.BeerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class BeerServiceImpl implements BeerService {

    private final BeerRepository repository;
    private final BeerMapper beerMapper;
    private final RatingServiceImpl ratingService;

    @Autowired
    public BeerServiceImpl(BeerRepository repository, BeerMapper beerMapper, RatingServiceImpl ratingService) {
        this.repository = repository;
        this.beerMapper = beerMapper;
        this.ratingService = ratingService;
    }

    @Transactional
    @Override
    public List<Beer> getAll() {
        return repository.findAll();
    }

    @Transactional
    @Override
    public Beer getById(Long id) {

        return repository.findById(id).get();

    }

    @Transactional
    @Override
    public Beer getByName(String name) {

        return repository.getByName(name);
    }

    @Transactional
    @Override
    public void createBeer(Beer beer) {
        boolean duplicateExists = true;

        try {
            repository.getByName(beer.getName());

        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new DuplicateEntityException("Beer", "name", beer.getName());
        }
        repository.save(beer);
    }


    @Transactional
    @Override
    public void updateBeer(Beer beer, User user) {
        verifyCanModify(beer, user);

        boolean duplicateExists = true;

        try {
            Beer existingBeer = repository.getByName(beer.getName());
            if (existingBeer.getId() == beer.getId()) {
                duplicateExists = false;
            }
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new DuplicateEntityException("Beer", "name", beer.getName());
        }

        repository.save(beer);
    }
    @Transactional
    @Override
    public void delete(Long id, User user) {
        Beer beer = repository.findById(id).get();

        if (!beer.getCreatedBy().equals(user) || !user.hasRole("ADMIN")) {
            throw new UnauthorizedOperationException("Only creator or admin can modify a beer");
        }
        repository.deleteById(id);

    }

    @Override
    public List<BeerResponseDTO> searchBeers(String name, String type, Double minAbv, Double maxAbv, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Specification<Beer> spec = Specification.where(null);

        if (name != null) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }
        if (minAbv != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("abv"), minAbv));
        }
        if (maxAbv != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("abv"), maxAbv));
        }

        return repository.findAll(spec, sort).stream()
                .map(beer -> {
                    // 1️⃣ Map base fields
                    BeerResponseDTO dto = beerMapper.toBeerResponseDTO(beer);

                    // 2️⃣ Enrich with computed data (average rating)
                    Double avgRating = ratingService.getAvgRating(beer);
                    dto.setAverageRating(avgRating);

                    return dto;
                })
                .toList();
    }

    private void verifyCanModify(Beer beer, User user) {
        if (!beer.getCreatedBy().equals(user) && !user.hasRole("ADMIN")) {
            throw new UnauthorizedOperationException("Only creator and admin can modify a beer");
        }
    }

}
