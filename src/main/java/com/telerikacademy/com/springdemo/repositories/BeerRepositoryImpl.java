package com.telerikacademy.com.springdemo.repositories;
import com.telerikacademy.com.springdemo.exceptions.DuplicateEntityException;
import com.telerikacademy.com.springdemo.exceptions.DuplicateNonUnique;
import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.models.Beer;

import jakarta.persistence.*;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BeerRepositoryImpl implements BeerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Beer> getAll(){

            TypedQuery<Beer> query = entityManager.createQuery("from Beer", Beer.class);

            return query.getResultList();
    }

    @Override
    public List<Beer> filter(Optional<String> name, Optional<Double> MinAbv) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Beer getById(int id){

         Beer beer = entityManager.find(Beer.class, id);
         if (beer==null){
             throw new EntityNotFoundException("Beer", id);
         }
         return beer;
    }


    @Override
    public Beer getByName(String name){
        try {
            TypedQuery<Beer> query = entityManager.createQuery("from Beer where name = :name", Beer.class);
            query.setParameter("name",name);
           return query.getSingleResult();
        }
        catch (NoResultException e){
            throw new EntityNotFoundException("Beer", "name", name);
        }
    }

    @Override
    public void create(Beer beer){
              entityManager.persist(beer);
    }

    @Override
    public void update(Beer beer) {
        try{
            entityManager.merge(beer);
        }
        catch (DuplicateNonUnique e){
            throw new DuplicateNonUnique("Beer", "name", beer.getName());
        }

    }

    @Override
    public void delete(int id) {

    }
}
