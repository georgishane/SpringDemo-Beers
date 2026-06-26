package com.telerikacademy.com.springdemo.controllers.rest;

import com.telerikacademy.com.springdemo.dto.request.CommentRequestDTO;
import com.telerikacademy.com.springdemo.dto.request.ReportCommentRequestDTO;
import com.telerikacademy.com.springdemo.dto.response.CommentResponseDTO;
import com.telerikacademy.com.springdemo.dto.response.ReportCommentResponseDTO;
import com.telerikacademy.com.springdemo.exceptions.UnauthorizedOperationException;
import com.telerikacademy.com.springdemo.mappers.ModelMapper;
import com.telerikacademy.com.springdemo.models.*;
import com.telerikacademy.com.springdemo.security.AppUserDetails;
import com.telerikacademy.com.springdemo.services.*;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final CommentReportService reportService;
    private final BeerService beerService;
    private final ModelMapper modelMapper;

    public CommentController(CommentService commentService, UserService userService, CommentReportService reportService, BeerService beerService, ModelMapper modelMapper) {
        this.commentService = commentService;
        this.userService = userService;
        this.reportService = reportService;
        this.beerService = beerService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/{beerId}")
    public ResponseEntity<CommentResponseDTO> addComment(
            @PathVariable Long beerId,
            @RequestBody CommentRequestDTO request,
            Authentication authentication) {

        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        Comment savedComment = commentService.createComment(user,beerId,request.getText());

        CommentResponseDTO response = CommentResponseDTO.builder()
                .id(savedComment.getId())
                .userName(savedComment.getUser().getUsername())
                .beerName(savedComment.getBeer().getName())
                .text(savedComment.getText())
                .createdAt(savedComment.getCreatedAt())
                .build();

        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long id,
            @RequestBody CommentRequestDTO dto,
            Principal principal) {

        Comment updatedComment = commentService.updateComment(id, dto, principal.getName());

        CommentResponseDTO response = CommentResponseDTO.builder()
                .id(updatedComment.getId())
                .userName(updatedComment.getUser().getUsername())
                .beerName(updatedComment.getBeer().getName())
                .text(updatedComment.getText())
                .createdAt(updatedComment.getCreatedAt())
                .build();

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId,
                                           Authentication authentication) {
        User user = userService.getByUserName(authentication.getName());

        commentService.deleteComment(commentId, user);
        return ResponseEntity.ok(Map.of("message", "Comment deleted successfully"));
    }
    @PostMapping("/{commentId}/report")
    public ResponseEntity<ReportCommentResponseDTO> reportComment(
            @PathVariable Long commentId,
            @RequestBody ReportCommentRequestDTO request,
            Authentication authentication) {

        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        Logger.getLogger(CommentController.class.getName()).info("request"+request);
        CommentReport savedReport = reportService.reportComment(commentId, user, request.getReason());

        ReportCommentResponseDTO response = ReportCommentResponseDTO.builder()
                .id(savedReport.getId())
                .commentId(savedReport.getComment().getId())
                .reportedBy(savedReport.getReportedBy().getUsername())
                .reason(savedReport.getReason())
                .reportedAt(savedReport.getReportedAt())
                .build();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/beer/{beerId}")
    public ResponseEntity<?> getCommentsByBeer(@PathVariable Long beerId) {
        List<Comment> comments = commentService.getCommentsByBeer(beerId);

        List<Map<String, Object>> response = comments.stream().map(comment -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("commentId", comment.getId());
            map.put("user", comment.getUser().getUsername());
            map.put("beerName", comment.getBeer().getName());
            map.put("text", comment.getText());
            return map;
        }).toList();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchComments(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) String beerName,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        String loggedInUser = authentication.getName();
        User currentUser = userService.getByUserName(loggedInUser);

        // Only the user or an admin can access
        if (!loggedInUser.equals(userName) && !currentUser.hasRole("ADMIN")) {
            throw new UnauthorizedOperationException("You cannot view these comments");
        }

        Page<Comment> resultPage = commentService.searchComments(
                text, beerName, userName, fromDate, toDate, sortBy, sortDir, page, size);

        List<CommentResponseDTO> comments = resultPage.getContent()
                .stream()
                .map(c -> CommentResponseDTO.builder()
                        .id(c.getId())
                        .userName(c.getUser().getUsername())
                        .beerName(c.getBeer().getName())
                        .text(c.getText())
                        .createdAt(c.getCreatedAt())
                        .build())
                .toList();

        Map<String, Object> response = Map.of(
                "comments", comments,
                "currentPage", resultPage.getNumber(),
                "totalItems", resultPage.getTotalElements(),
                "totalPages", resultPage.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

}

