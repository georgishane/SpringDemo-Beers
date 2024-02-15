package com.telerikacademy.com.springdemo.repositories;

import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.models.Beer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//@Repository

public class BeerRepositorySqlImpl implements BeerRepository{
    private final DbHelper dbHelper;

    public BeerRepositorySqlImpl(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public List<Beer> getAll() {

        try (Connection connection = dbHelper.getConnection()) {
            String sql = "SELECT * FROM beers";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
               return getBeers(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving beers from the database: " + e.getMessage(), e);
        }

    }

    @Override
    public List<Beer> filter(Optional<String> name, Optional<Double> minAbv) {

            String query = "SELECT * FROM beers WHERE 1=1";

            if (name.isPresent()){
                query += " AND name LIKE '%?%'";
            }
            if (minAbv.isPresent()){
                query +=" AND abv > ?";
            }

            try (Connection connection = dbHelper.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                int paramIndex = 1;

                if (name.isPresent()){
                    preparedStatement.setString(paramIndex, name.get());
                    paramIndex++;
                }
                if (minAbv.isPresent()){
                    preparedStatement.setDouble(paramIndex, minAbv.get());
                    paramIndex++;
                }
                try (ResultSet resultSet = preparedStatement.executeQuery()){
                return getBeers(resultSet);
                }
            }

            catch (SQLException e){
                throw new RuntimeException(e);
            }
        }

    @Override
    public Beer getById(int id) {
        String sql = "SELECT beer_id, name, abv FROM beers WHERE beer_id = ?";

        try (Connection connection = dbHelper.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                List<Beer> resultList = getBeers(resultSet);
                if (resultList.isEmpty()){
                    throw new EntityNotFoundException("Beer", id);
                }
                return resultList.get(0);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving beers from the database: " + e.getMessage(), e);
        }

    }

    @Override
    public Beer getByName(String name) {

        String sql = "SELECT beer_id, name, abv FROM beers WHERE name = ?";

        try (Connection connection = dbHelper.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1,name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                List<Beer> resultList = getBeers(resultSet);
                if (resultList.isEmpty()){
                    throw new EntityNotFoundException("Beer","name", name);
                }
                return resultList.get(0);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving beers from the database: " + e.getMessage(), e);
        }

    }

    @Override
    public void create(Beer beer) {
        String sql = "INSERT INTO beers (name, abv)" + "values(?, ?)";

        try (Connection connection = dbHelper.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, beer.getName());
            preparedStatement.setDouble(2, beer.getAbv());
            preparedStatement.executeQuery();

            Beer newBeer = getByName(beer.getName());
            beer.setId(newBeer.getId());

        } catch (SQLException e) {
            throw new RuntimeException("Error creating beer in the database: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Beer beer) {

    }

    @Override
    public void delete(int id) {

    }
   private List<Beer> getBeers(ResultSet resultSet) throws SQLException {
       List<Beer> beers = new ArrayList<>();
       while (resultSet.next()) {
           Beer beer = new Beer();
           // Populate Beer object from the ResultSet
           beer.setId(resultSet.getInt("beer_id"));
           beer.setName(resultSet.getString("name"));
           beer.setAbv(resultSet.getDouble("abv"));
           // ... other fields

           beers.add(beer);
       }
       return beers;

   }
}
