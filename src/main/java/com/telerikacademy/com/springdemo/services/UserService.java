package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.User;

import java.util.List;

public interface UserService {
    User getById(int id);

    List<User> getAll();

    void addToWishList(User user, Beer beer);

    User getByUserName(String username);
}
