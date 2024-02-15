package com.telerikacademy.com.springdemo.repositories;
import java.util.List;
public interface Repository <T>{

    List<T> getAll();

    T getById(int id);

    void create(T beer);

    void update(T beer);

    void delete(int id);
}
