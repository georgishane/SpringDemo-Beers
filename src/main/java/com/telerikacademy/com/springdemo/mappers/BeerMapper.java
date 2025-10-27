package com.telerikacademy.com.springdemo.mappers;

import com.telerikacademy.com.springdemo.models.Beer;

import com.telerikacademy.com.springdemo.models.BeerResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class BeerMapper {

    public BeerResponseDTO toBeerResponseDTO(Beer beer) {
        BeerResponseDTO  dto = new BeerResponseDTO();
        dto.setId(beer.getId());
        dto.setName(beer.getName());
        dto.setStyle(beer.getStyle());
        dto.setAbv(beer.getAbv());
        return dto;
    }
}

