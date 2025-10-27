package com.telerikacademy.com.springdemo.repositories;
import com.telerikacademy.com.springdemo.models.Beer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface BeerRepository extends JpaRepository<Beer, Long>, JpaSpecificationExecutor<Beer> {

    List<Beer> filter(Optional<String> name, Optional<Double> MinAbv);

    Beer getByName(String name);

}
