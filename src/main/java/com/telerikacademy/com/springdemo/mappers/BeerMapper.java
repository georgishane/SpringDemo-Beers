package com.telerikacademy.com.springdemo.mappers;

import com.telerikacademy.com.springdemo.models.Beer;

import com.telerikacademy.com.springdemo.dto.request.BeerDetailsDTO;
import com.telerikacademy.com.springdemo.dto.response.BeerResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class BeerMapper {

    public BeerResponseDTO toBeerResponseDTO(Beer beer) {
        BeerResponseDTO  dto = new BeerResponseDTO();
        dto.setId(beer.getId());
        dto.setName(beer.getName());
        dto.setAbv(beer.getAbv());
        // Convert Style entity → String
        if (beer.getStyle() != null) {
            dto.setStyle(beer.getStyle().getName());
        }
        dto.setAbv(beer.getAbv());
        return dto;
    }
    public BeerDetailsDTO toDetailsDto(Beer beer) {
        BeerDetailsDTO dto = new BeerDetailsDTO();
        dto.setId(beer.getId());
        dto.setName(beer.getName());
//        dto.setBrewery(beer.getBrewery());
        dto.setAbv(beer.getAbv());
        dto.setStyle(beer.getStyle().getName());
        return dto;
    }
}

