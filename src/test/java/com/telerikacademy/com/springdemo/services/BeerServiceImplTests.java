package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.exceptions.DuplicateEntityException;
import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.exceptions.UnauthorizedOperationException;
import com.telerikacademy.com.springdemo.models.Beer;

import com.telerikacademy.com.springdemo.models.Role;
import com.telerikacademy.com.springdemo.models.User;
import com.telerikacademy.com.springdemo.repositories.BeerRepository;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static com.telerikacademy.com.springdemo.Helpers.createMockBeer;
import static com.telerikacademy.com.springdemo.Helpers.createMockUser;


@ExtendWith(MockitoExtension.class)
public class BeerServiceImplTests {

    @Mock
    BeerRepository mockBeerRepository;
    @InjectMocks
    BeerServiceImpl service;


    @Test
    public void getById_Should_ReturnBeer_When_MatchExists(){
        //Arrange

        Mockito.when(mockBeerRepository.getById(2))
                .thenReturn(new Beer(2, "MockBeerName", 2.2));
        //Act
        Beer result = service.getById(2);

        //Assert
        Assertions.assertEquals(2, result.getId());
        Assertions.assertEquals("MockBeerName", result.getName());
        Assertions.assertEquals(2.2, result.getAbv());
    }

    @Test
    public void create_Should_Throw_When_BeerWithSameNameExists(){
        //Arrange
        var mockBeer  = createMockBeer();
        Mockito.when(mockBeerRepository.getByName(mockBeer.getName()))
                .thenReturn(mockBeer);

        //Act, Assert

        Assertions.assertThrows(DuplicateEntityException.class,
                ()-> service.createBeer(mockBeer));

    }

    @Test
    public void create_Should_CallRepo_When_BeerWithSameNameExists(){

       // Arrange

        var mockBeer  = createMockBeer();

        Mockito.lenient().when(mockBeerRepository.getByName(mockBeer.getName()))
                .thenThrow(new EntityNotFoundException("Beer", "name", mockBeer.getName()));


        // Act
            service.createBeer(mockBeer);
        // Assert

        Mockito.verify(mockBeerRepository, Mockito.times(1))
                .create(Mockito.any(Beer.class));
    }

    @Test
    public void update_Should_Call_Repo_When_UserIsCreator(){
        //Arrange
        var mockUser = createMockUser();
        var mockBeer = createMockBeer();
        mockBeer.setCreatedBy(mockUser);

        Mockito.when(mockBeerRepository.getByName(mockBeer.getName()))
                .thenReturn(mockBeer);
        //Act
       service.updateBeer(mockBeer, mockUser);
        // Assert

        Mockito.verify(mockBeerRepository, Mockito.times(1))
                .update(mockBeer);
    }

    @Test
    public void update_Should_Call_Repo_When_UserIsAdmin(){
        //Arrange
        var mockAdmin = createMockUser();
        mockAdmin.setRoles(Set.of(new Role("Admin")));

        var mockBeer = createMockBeer();
        mockBeer.setCreatedBy(new User());

        Mockito.when(mockBeerRepository.getByName(mockBeer.getName()))
                .thenReturn(mockBeer);
        //Act
        service.updateBeer(mockBeer, mockAdmin);
        // Assert

        Mockito.verify(mockBeerRepository, Mockito.times(1))
                .update(mockBeer);
    }

    @Test
    public void update_Should_Throw_When_UserIsNotCreatorOrAdmin(){
        //Arrange
        var mockBeer = createMockBeer();
        var mockUser = createMockUser();

        mockBeer.setCreatedBy(mockUser);

        mockBeer.setCreatedBy(new User());
        // Act, Assert

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> service.updateBeer(mockBeer,mockUser));
    }
    @Test
    public void update_Should_Throw_When_BeerNameIsTaken(){

        var mockUser = createMockUser();

        var mockBeer = createMockBeer();
        mockBeer.setCreatedBy(mockUser);

        var anotherMockBeer = createMockBeer();
        anotherMockBeer.setId(2);

        Mockito.lenient().when(mockBeerRepository.getById(Mockito.anyInt()))
                        .thenReturn(mockBeer);

        Mockito.lenient().when(mockBeerRepository.getByName(Mockito.anyString()))
                .thenReturn(anotherMockBeer);

        Assertions.assertThrows(DuplicateEntityException.class,
                ()-> service.updateBeer(mockBeer, mockUser));
    }

    @Test
    public void update_Should_Throw_When_BeerWithSameNameExists(){
        //Arrange
        var beerToUpdate  = createMockBeer();
        var existingBeer = createMockBeer();
        existingBeer.setId(beerToUpdate.getId()+1);

        Mockito.when(mockBeerRepository.getByName(beerToUpdate.getName()))
                .thenReturn(existingBeer);

        //Act, Assert
        Assertions.assertThrows(DuplicateEntityException.class,
                ()-> service.updateBeer(beerToUpdate, beerToUpdate.getCreatedBy()));

    }

    @Test
    public void update_Should_Call_Repo_When_NameIsUnique(){
        //Arrange
        var beerToUpdate  = createMockBeer();

        Mockito.when(mockBeerRepository.getByName(beerToUpdate.getName()))
                .thenThrow(EntityNotFoundException.class);

        //Act
        service.updateBeer(beerToUpdate, beerToUpdate.getCreatedBy());
        // Assert
        Mockito.verify(mockBeerRepository,
                Mockito.times(1))
                .update(beerToUpdate);

    }

    @Test
    public void update_Should_Call_Repo_When_UpdatingSameBeer(){
        //Arrange
        var beerToUpdate  = createMockBeer();

        Mockito.when(mockBeerRepository.getByName(beerToUpdate.getName()))
                .thenReturn(beerToUpdate);

        //Act
        service.updateBeer(beerToUpdate, beerToUpdate.getCreatedBy());
        // Assert
        Mockito.verify(mockBeerRepository,
                        Mockito.times(1))
                .update(beerToUpdate);

    }

}
