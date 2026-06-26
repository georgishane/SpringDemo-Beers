package com.telerikacademy.com.springdemo.mappers;


import com.telerikacademy.com.springdemo.dto.request.CommentRequestDTO;
import com.telerikacademy.com.springdemo.dto.response.BeerDto;
import com.telerikacademy.com.springdemo.dto.response.CommentResponseDTO;
import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.models.*;
import com.telerikacademy.com.springdemo.repositories.BeerRepository;
import com.telerikacademy.com.springdemo.repositories.CommentRepository;
import com.telerikacademy.com.springdemo.repositories.StyleRepository;
import com.telerikacademy.com.springdemo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ModelMapper {

    private final BeerRepository beerRepository;
    private final StyleRepository styleRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ModelMapper(BeerRepository beerRepository, StyleRepository styleRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.beerRepository = beerRepository;
        this.styleRepository = styleRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public Beer fromDto(BeerDto beerDto){
        Beer beer = new Beer();
        dtoToObject(beerDto, beer);
        return beer;
    }

    public Beer fromDto(BeerDto beerDto, Long id){
        Beer beer = beerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Beer not found with id: " + id));
        dtoToObject(beerDto, beer);
        return beer;
    }

    private void dtoToObject(BeerDto beerDto, Beer beer){
        Style style = styleRepository.getById(beerDto.getStyleId());
        beer.setName(beerDto.getName());
        beer.setAbv(beerDto.getAbv());
        beer.setStyle(style);
    }

    private void dtoToObject(CommentRequestDTO commentRequestDTO, Comment comment){
        String userName = comment.getUser().getUsername();
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new EntityNotFoundException("User"+userName));
        comment.setUser(user);
        comment.setText(commentRequestDTO.getText());
    }
    public Comment fromDto(CommentRequestDTO commentRequestDTODto, Long commentId){
        Comment comment = commentRepository.findCommentById(commentId);
        dtoToObject(commentRequestDTODto, comment);
        return comment;
    }
    public CommentResponseDTO toDto(Comment comment) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(comment.getId());
        dto.setUserName((comment.getUser().getUsername()));
        dto.setText(comment.getText());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
