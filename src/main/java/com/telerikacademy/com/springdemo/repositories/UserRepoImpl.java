package com.telerikacademy.com.springdemo.repositories;

import com.telerikacademy.com.springdemo.exceptions.DuplicateNonUnique;
import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class UserRepoImpl implements UserRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> getAll() {

        TypedQuery<User> query = entityManager.createQuery("from User", User.class);

        return query.getResultList();

    }

    @Override
    public User getById(int id) {
        User user = entityManager.find(User.class,id);
        if (user==null){
            throw new EntityNotFoundException("User", id);
        }
        return user;
    }

    @Override
    public void update(User user) {
            entityManager.merge(user);
    }

    @Override
    public User getByUserName(String username) {
//        User user = entityManager.find(User.class, username);
//        if (user == null){
//            throw new EntityNotFoundException("User", username);
//        }
//        return user;

        TypedQuery<User> query = entityManager.createQuery("from User where username = :username", User.class);
        query.setParameter("username", username);
        List<User> users = query.getResultList();
        if (users.isEmpty()){
            throw new EntityNotFoundException("User", "username", username);
        }
        return users.get(0);
    }
}
