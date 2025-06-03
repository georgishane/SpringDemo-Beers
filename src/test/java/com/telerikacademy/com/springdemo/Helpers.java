package com.telerikacademy.com.springdemo;

import com.telerikacademy.com.springdemo.models.*;

import java.util.Set;

public class Helpers {

    public static User createMockUser(){
    var mockUser = new User();
    var personalInfo = new PersonalInfo();
    personalInfo.setId(1);
    personalInfo.setFirstName("MockFirstName");
    personalInfo.setLastName("MockLastName");
    personalInfo.setEmail("mock@user.com");
    mockUser.setPersonalInfo(personalInfo);
    mockUser.setUserId(1);
    mockUser.setUsername("MockUsername");
    mockUser.setPassword("MockPass");
    mockUser.setRoles(Set.of(createMockRole()));
    return mockUser;
    }

    public static Beer createMockBeer(){
        var mockBeer = new Beer();
        mockBeer.setId(1);
        mockBeer.setName("TestBeer");
        mockBeer.setCreatedBy(createMockUser());
        mockBeer.setStyle(createMockStyle());
        return mockBeer;
    }

    public static Role createMockRole(){
        var mockRole = new Role("Admin");
        mockRole.setRoleId(1);
        mockRole.setName("User");
        return mockRole;
    }

    public static Style createMockStyle(){
     var mockStyle = new Style();
     mockStyle.setId(1);
     mockStyle.setName("Pale");
     return mockStyle;

    }

}
