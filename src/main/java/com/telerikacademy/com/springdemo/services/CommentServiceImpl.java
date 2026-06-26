package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.exceptions.EntityNotFoundException;
import com.telerikacademy.com.springdemo.models.Beer;
import com.telerikacademy.com.springdemo.models.Comment;
import com.telerikacademy.com.springdemo.dto.request.CommentRequestDTO;
import com.telerikacademy.com.springdemo.models.User;
import com.telerikacademy.com.springdemo.repositories.BeerRepository;
import com.telerikacademy.com.springdemo.repositories.CommentRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final BeerRepository beerRepository;
    private final BeerService beerService;
    private final UserService userService;

    public CommentServiceImpl(CommentRepository commentRepository, BeerRepository beerRepository, BeerService beerService, UserService userService) {
        this.commentRepository = commentRepository;
        this.beerRepository = beerRepository;
        this.beerService = beerService;
        this.userService = userService;
    }

    @Transactional
    @Override
    public void deleteComment(Long commentId, User user) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (comment.getUser().getUserId()!=(user.getUserId())) {
            throw new AccessDeniedException("You can only delete your own comments.");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    @Override
    public List<Comment> getCommentsByBeer(Long beerId) {
        return commentRepository.findAllByBeerId(beerId);
    }

    @Transactional
    @Override
    public Comment createComment(User user, Long beerId, String text) {
        Beer beer = beerRepository.findById(beerId).orElseThrow(() -> new EntityNotFoundException("Beer not found with id: " + beerId));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setBeer(beer);
        comment.setText(text);
        comment.setCreatedAt(LocalDateTime.now());
       return commentRepository.save(comment);
    }
    @Transactional
    @Override
    public Comment updateComment(Long id, CommentRequestDTO dto, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment",id));

        // Allow only the comment owner or admin
        if (!comment.getUser().getUsername().equals(username) &&
                !userService.getByUserName(username).hasRole("ADMIN")
                        ) {
            throw new AccessDeniedException("You cannot edit this comment");
        }

        comment.setText(dto.getText());
        comment.setUpdatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByUser(String username) {
        User user = userService.getByUserName(username);
        return commentRepository.findByUser(user);
    }


    @Override
    public Page<Comment> searchComments(String text, String beerName, String userName,
                                        String fromDate, String toDate,
                                        String sortBy, String sortDir,
                                        int page, int size
    ) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        LocalDateTime from = (fromDate != null) ? LocalDate.parse(fromDate).atStartOfDay() : null;
        LocalDateTime to = (toDate != null) ? LocalDate.parse(toDate).atTime(23, 59, 59) : null;

        return commentRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (text != null) {
                predicates.add(cb.like(cb.lower(root.get("text")), "%" + text.toLowerCase() + "%"));
            }
            if (beerName != null) {
                predicates.add(cb.like(cb.lower(root.get("beer").get("name")), "%" + beerName.toLowerCase() + "%"));
            }
            if (userName != null) {
                predicates.add(cb.like(cb.lower(root.get("user").get("username")), "%" + userName.toLowerCase() + "%"));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), to));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

}

