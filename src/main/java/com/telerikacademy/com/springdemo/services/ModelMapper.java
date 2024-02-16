package com.telerikacademy.com.springdemo.services;


import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.BeerDto;
import com.telerikacademy.com.springdemo.models.Style;
import com.telerikacademy.com.springdemo.repositories.BeerRepository;
import com.telerikacademy.com.springdemo.repositories.StyleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

    private final BeerRepository beerRepository;
    private final StyleRepository styleRepository;

    @Autowired
    public ModelMapper(BeerRepository beerRepository, StyleRepository styleRepository) {
        this.beerRepository = beerRepository;
        this.styleRepository = styleRepository;
    }

    public Beer fromDto(BeerDto beerDto){
        Beer beer = new Beer();
        dtoToObject(beerDto, beer);
        return beer;
    }

    public Beer fromDto(BeerDto beerDto, int id){
        Beer beer = beerRepository.getById(id);
        dtoToObject(beerDto, beer);
        return beer;
    }

    private void dtoToObject(BeerDto beerDto, Beer beer){
        Style style = styleRepository.getById(beerDto.getStyleId());
        beer.setName(beerDto.getName());
        beer.setAbv(beerDto.getAbv());
        beer.setStyle(style);
    }
}
