package com.telerikacademy.com.springdemo.controllers.rest;

import com.telerikacademy.com.springdemo.controllers.AuthenticationHelper;
import com.telerikacademy.com.springdemo.dto.request.UserProfileDto;
import com.telerikacademy.com.springdemo.dto.request.UserUpdateDto;
import com.telerikacademy.com.springdemo.dto.response.CommentResponseDTO;
import com.telerikacademy.com.springdemo.exceptions.DuplicateEntityException;
import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.exceptions.UnauthorizedOperationException;
import com.telerikacademy.com.springdemo.models.*;
import com.telerikacademy.com.springdemo.repositories.UserRepository;
import com.telerikacademy.com.springdemo.security.AppUserDetails;
import com.telerikacademy.com.springdemo.services.BeerService;
import com.telerikacademy.com.springdemo.services.CommentService;
import com.telerikacademy.com.springdemo.services.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {


    private final UserService userService;
    private final BeerService beerService;
    private final AuthenticationHelper authenticationHelper;
    private final UserRepository userRepository;
    private final CommentService commentService;

    @Autowired
    public UserController(UserService userService, BeerService beerService, AuthenticationHelper authenticationHelper, AuthenticationHelper authenticationHelper1, UserRepository userRepository, CommentService commentService) {
        this.userService = userService;
        this.beerService = beerService;

        this.authenticationHelper = authenticationHelper1;
        this.userRepository = userRepository;

        this.commentService = commentService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<User> getAll(){
        return userService.getAll();
    }


    @GetMapping("/{id}")
    public User getUserById(@RequestHeader HttpHeaders headers, @PathVariable Long id){
        try {
            authenticationHelper.tryGetUser(headers);
            return userService.getById(id);
        } catch (EntityNotFoundException e) {
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND
                    ,e.getMessage());
        }

    }

    @GetMapping("/{id}/wish-list")
    public Set<Beer> getUserWishList(@RequestHeader HttpHeaders headers, @PathVariable Long id){
        try {
            return getUserById(headers,id).getWishList();
        } catch (EntityNotFoundException e) {
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND
                    ,e.getMessage());
        }
    }

    @PostMapping("/{userId}/wish-list")
    public void addBeerToWishList(@RequestHeader HttpHeaders headers, @PathVariable Long userId,
                                       @Valid @RequestBody Map<String, Object> body){

        var user = getUserById(headers, userId);
        var beerId = (Long) body.get("beerId");

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
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUserProfile(Authentication authentication) {
        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        Logger.getLogger(UserController.class.getName()).info("User: " + user);
        PersonalInfo personalInfo = user.getPersonalInfo();

        Logger.getLogger(UserController.class.getName()).info("PersonalInfo: " + personalInfo);
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        UserProfileDto dto = new UserProfileDto(
                user.getUserId(),
                user.getUsername(),
                personalInfo.getEmail(),
                roles
        );

        return ResponseEntity.ok(dto);
    }
    @PutMapping("/me")
    public ResponseEntity<UserProfileDto> updateCurrentUserProfile(
            @RequestBody @Valid UserUpdateDto updateDto,
            Authentication authentication) {

        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // Update personal info (create it if null)
        PersonalInfo personalInfo = user.getPersonalInfo();

        Logger.getLogger(UserController.class.getName()).info("PersonalInfo: " + personalInfo);
        if (personalInfo == null) {
            personalInfo = new PersonalInfo();
            user.setPersonalInfo(personalInfo);
        }

        personalInfo.setEmail(updateDto.getEmail());
        personalInfo.setFirstName(updateDto.getFirstName());
        personalInfo.setLastName(updateDto.getLastName());

        // Save the updated user
        userRepository.save(user);

        // Prepare and return updated profile DTO
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        UserProfileDto updatedDto = new UserProfileDto(
                user.getUserId(),
                user.getUsername(),
                personalInfo.getEmail(),
                roles
        );

        return ResponseEntity.ok(updatedDto);
    }
    @GetMapping("/{username}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByUser(
            @PathVariable String username,
            Authentication authentication) {

        String loggedInUser = authentication.getName();
        User currentUser = userService.getByUserName(loggedInUser);

        // Only the user or an admin can access
        if (!loggedInUser.equals(username) && !currentUser.hasRole("ADMIN")) {
            throw new UnauthorizedOperationException("You cannot view these comments");
        }

        List<CommentResponseDTO> comments = commentService.getCommentsByUser(username)
                .stream()
                .map(c -> CommentResponseDTO.builder()
                        .id(c.getId())
                        .userName(c.getUser().getUsername())
                        .beerName(c.getBeer().getName())
                        .text(c.getText())
                        .createdAt(c.getCreatedAt())
                        .build())
                .toList();

        return ResponseEntity.ok(comments);
    }

}
