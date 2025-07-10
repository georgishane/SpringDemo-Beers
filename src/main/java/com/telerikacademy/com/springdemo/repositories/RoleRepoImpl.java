package com.telerikacademy.com.springdemo.repositories;

import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.models.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleRepoImpl implements RoleRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Role getByName(String name) {
        TypedQuery<Role> query = entityManager.createQuery("FROM Role WHERE UPPER(name) = :name", Role.class);
        query.setParameter("name", name.toUpperCase());

        List<Role> roles = query.getResultList();
        if (roles.isEmpty()) {
            throw new EntityNotFoundException("Role", "name", name);
        }
        return roles.get(0);
    }
}
