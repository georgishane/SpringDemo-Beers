package com.telerikacademy.com.springdemo.repositories;

import com.telerikacademy.com.springdemo.models.User;

import java.util.List;

public interface UserRepository {
    List <User> getAll();
    User getById(int id);

    void update(User user);

    User getByUserName(String username);
}
