package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.exceptions.DuplicateEntityException;
import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.User;
import com.telerikacademy.com.springdemo.repositories.UserRepository;
import com.telerikacademy.com.springdemo.security.AppUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import java.util.List;

@Service
public class UserServiceImpl implements UserService, UserDetailsService{

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public User getById(Long id) {

       return repository.findById(id)
               .orElseThrow(()-> new EntityNotFoundException("User",id));
    }

    @Transactional
    @Override
    public List<User> getAll(){
       return repository.findAll();
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
        repository.save(user);
    }

    @Override
    public User getByUserName(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User",username));
    }

    @Override
    public void create(User user) {
        repository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

                    User user = repository.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User", username));
            return new AppUserDetails(user);

    }

    public User register(String username, String rawPassword) {
        if (repository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists.");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));

        return repository.save(user);
    }
}
