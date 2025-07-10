package com.telerikacademy.com.springdemo.controllers.rest;

import com.telerikacademy.com.springdemo.controllers.AuthenticationHelper;
import com.telerikacademy.com.springdemo.exceptions.DuplicateEntityException;
import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.BeerDto;
import com.telerikacademy.com.springdemo.models.User;
import com.telerikacademy.com.springdemo.services.BeerService;
import com.telerikacademy.com.springdemo.services.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {


    private final UserService userService;
    private final BeerService beerService;

    private final AuthenticationHelper authenticationHelper;
    @Autowired
    public UserController(UserService userService, BeerService beerService, AuthenticationHelper authenticationHelper, AuthenticationHelper authenticationHelper1) {
        this.userService = userService;
        this.beerService = beerService;

        this.authenticationHelper = authenticationHelper1;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<User> getAll(){
        return userService.getAll();
    }

      @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/test-admin")
    public String adminOnlyTest() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authenticated user: " + auth.getName());
        System.out.println("Authorities: " + auth.getAuthorities());
        return "You are an admin!";
    }

    @GetMapping("/{id}")
    public User getUserById(@RequestHeader HttpHeaders headers, @PathVariable int id){
        try {
            authenticationHelper.tryGetUser(headers);
            return userService.getById(id);
        } catch (EntityNotFoundException e) {
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND
                    ,e.getMessage());
        }

    }

    @GetMapping("/{id}/wish-list")
    public Set<Beer> getUserWishList(@RequestHeader HttpHeaders headers, @PathVariable int id){
        try {
            return getUserById(headers,id).getWishList();
        } catch (EntityNotFoundException e) {
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND
                    ,e.getMessage());
        }
    }

    @PostMapping("/{userId}/wish-list")
    public void addBeerToWishList(@RequestHeader HttpHeaders headers, @PathVariable int userId,
                                       @Valid @RequestBody Map<String, Object> body){

        var user = getUserById(headers, userId);
        var beerId = (int)body.get("beerId");

        Beer beer;
        try
        {beer = beerService.getById(beerId);
        }
        catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

        try {
            userService.addToWishList(user, beer);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

    }
}
