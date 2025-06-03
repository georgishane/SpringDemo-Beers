package com.telerikacademy.com.springdemo.repositories;

import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.Rating;
import com.telerikacademy.com.springdemo.models.User;
import jakarta.persistence.*;
import org.springframework.cache.support.NullValue;
import org.springframework.stereotype.Repository;

@Repository
public class RatingRepoImpl implements RatingRepository {

    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public void update(Rating rating){

        entityManager.merge(rating);
    }

    @Override
    public void create(Rating rating){

        entityManager.persist(rating);
    }


    @Override
    public Rating getByUserAndBeer(User user, Beer beer) {

        TypedQuery<Rating> query = entityManager.createQuery("from Rating" +
                " where user = :user and beer = :beer", Rating.class);
        query.setParameter("user", user);
        query.setParameter("beer", beer);

         var result = query.getResultList();
         if (result.isEmpty()){
             return null;
         }

         return result.get(0);
    }

    @Override
    public boolean ratingExistsForUserAndBeer(User user, Beer beer) {

        try{
            getByUserAndBeer(user, beer);

        }
        catch (EntityNotFoundException e){
            return false;
        }

        return true;
    }

    @Override
    public Double getAverage(Beer beer){
        TypedQuery<Double> query = entityManager.createQuery("select avg(rating)" +
                "from Rating where beer = :beer", Double.class);
        query.setParameter("beer", beer);
         try {
             return query.getSingleResult();
         }
         catch (NoResultException e) {
             throw new EntityNotFoundException("Beer", beer.getName());
         }

    }
}
