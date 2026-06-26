package com.telerikacademy.com.springdemo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username")
    private String username;

    @JsonIgnore
    @Column(name = "password",length = 255, nullable = false)
    private String password;


   @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_beers",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "beer_id")
    )
    private Set<Beer> wishList;

   @OneToOne(cascade = CascadeType.ALL)
   @JoinColumn(
           name = "personal_info"
   )
   private PersonalInfo personalInfo;

    @ManyToMany(fetch = FetchType.EAGER)
   @JoinTable(
           name = "users_roles",
           joinColumns = @JoinColumn(name = "user_id"),
           inverseJoinColumns = @JoinColumn(name = "role_id")
   )
   private Set<Role> roles;

    public User(){

    }
    public boolean hasRole(String roleName){

        return getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase(roleName));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(wishList, user.wishList) && Objects.equals(personalInfo, user.personalInfo) && Objects.equals(roles, user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, password, wishList, personalInfo, roles);
    }
}
