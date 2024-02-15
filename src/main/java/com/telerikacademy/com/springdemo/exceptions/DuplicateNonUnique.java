package com.telerikacademy.com.springdemo.exceptions;

import jakarta.persistence.PersistenceException;

public class DuplicateNonUnique extends PersistenceException {
    public DuplicateNonUnique(String type, String attribute, String value) {
        super(String.format("%s with this %s %s already exists.", type, attribute, value));
    }
}
