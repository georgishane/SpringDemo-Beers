package com.telerikacademy.com.springdemo.security;

import com.telerikacademy.com.springdemo.models.Role;
import com.telerikacademy.com.springdemo.models.User;
import com.telerikacademy.com.springdemo.services.RefreshTokenService;
import org.apache.logging.log4j.LogManager;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.stream.Collectors;
import java.util.logging.Logger;

public class AppUserDetails implements UserDetails {


    private final User user;

    public AppUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Debug printout

        Logger.getLogger(AppUserDetails.class.getName()).info("User role from DB: " + user.getRoles());

        return user.getRoles().stream()
                .map(Role::getName)
                .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
