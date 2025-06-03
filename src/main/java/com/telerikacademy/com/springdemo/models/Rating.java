package com.telerikacademy.com.springdemo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "beers_ratings")
public class Rating {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   // @SequenceGenerator(name = "beers_ratings_seq", sequenceName = "beers_ratings_seq", allocationSize = 1)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "beer_id")
    private Beer beer;

    @Column
    private double rating;

    public Rating() {
    }

    public Rating(User user, Beer beer, double rating) {
        setUser(user);
        setBeer(beer);
        setRating(rating);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Beer getBeer() {
        return beer;
    }

    public void setBeer(Beer beer) {
        this.beer = beer;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
