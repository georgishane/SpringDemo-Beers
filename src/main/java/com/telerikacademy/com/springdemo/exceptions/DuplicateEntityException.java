package com.telerikacademy.com.springdemo.exceptions;

public class DuplicateEntityException extends RuntimeException{
    public DuplicateEntityException(String type, String attribute, String value) {
        super(String.format("%s with this %s %s already exists.", type, attribute, value));
    }
    public DuplicateEntityException(String message){
        super (message);
    }
}
