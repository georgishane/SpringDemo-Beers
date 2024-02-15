package com.telerikacademy.com.springdemo.models;

import org.springframework.data.annotation.Id;
import jakarta.persistence.*;


@Entity
@Table(name = "beers")
public class  Beer {

    @jakarta.persistence.Id
    @Id
    @Column(name = "beer_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")

    private String name;

    @Column(name = "abv")

    private double abv;

    public Beer(int id, String name, double abv) {
        this.id = id;
        this.name = name;
        this.abv = abv;
    }

    public Beer() {

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getAbv() {
        return abv;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAbv(double abv) {
        this.abv = abv;
    }


}
