package com.telerikacademy.com.springdemo.exceptions;

public class UnauthorizedOperationException extends RuntimeException{

    public UnauthorizedOperationException(String message){

        super(message);
    }
}
