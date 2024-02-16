package com.telerikacademy.com.springdemo.repositories;

import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.models.Style;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class StyleRepoImpl implements StyleRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Style getById(int id) {
        Style style = entityManager.find(Style.class,id);
        if (style==null){
            throw new EntityNotFoundException("Style", id);
        }
        return style;
    }
}
