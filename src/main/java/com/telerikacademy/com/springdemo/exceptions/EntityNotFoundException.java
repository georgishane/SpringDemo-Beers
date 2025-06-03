package com.telerikacademy.com.springdemo.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String type, int id) {
    this(type,"id",  String.valueOf(id));

    }

    public EntityNotFoundException(String type, String attribute, String value) {
        super(String.format("%s with %s %s not found", type, attribute, value));

    }

    public EntityNotFoundException(String user, String beer) {
        super(String.format("Rating with user %s and beer %s was not found.", user, beer));
    }

    public EntityNotFoundException(String beer) {
        super(String.format("Rating for beer %s was not found.", beer));
    }

}
