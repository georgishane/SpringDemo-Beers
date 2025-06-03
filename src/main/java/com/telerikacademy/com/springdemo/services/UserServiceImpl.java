package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.exceptions.DuplicateEntityException;
import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.User;
import com.telerikacademy.com.springdemo.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Override
    public User getById(int id) {
        return repository.getById(id);
    }

    @Transactional
    @Override
    public List<User> getAll(){
       return repository.getAll();
    }


    @Transactional
    @Override
    public void addToWishList(User user, Beer beer) {
        var newWishList = user.getWishList();
        if (newWishList.contains(beer)){
            throw new DuplicateEntityException(String.format("Beer with id %s is already in the wishlist.", beer.getId()));
        }

        newWishList.add(beer);
        user.setWishList(newWishList);
        repository.update(user);
    }

    @Override
    public User getByUserName(String username) {
        return repository.getByUserName(username);
    }
}
