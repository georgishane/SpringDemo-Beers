package com.telerikacademy.com.springdemo.repositories;
import com.telerikacademy.com.springdemo.models.Beer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BeerRepository extends JpaRepository<Beer, Long>, JpaSpecificationExecutor<Beer> {

    Beer getByName(String name);

}
