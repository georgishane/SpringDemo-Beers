package com.telerikacademy.com.springdemo.repositories;

import com.telerikacademy.com.springdemo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

}
