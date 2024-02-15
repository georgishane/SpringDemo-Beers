package com.telerikacademy.com.springdemo.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
@PropertySource("classpath:application.properties")
public class DbHelper {
        private final String dbUrl, dbUsername, dbPassword;

        @Autowired
        public DbHelper(Environment env) {
            dbUrl = env.getProperty("datasource.url");
            dbUsername = env.getProperty("datasource.username");
            dbPassword = env.getProperty("datasource.password");
        }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public Connection getConnection() throws SQLException {

            return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }
}

