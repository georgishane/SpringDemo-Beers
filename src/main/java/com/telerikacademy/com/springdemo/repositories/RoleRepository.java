package com.telerikacademy.com.springdemo.repositories;

import com.telerikacademy.com.springdemo.models.Role;

public interface RoleRepository {
    Role getByName(String name);
}
