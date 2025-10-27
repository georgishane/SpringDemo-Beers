package com.telerikacademy.com.springdemo.controllers.rest;

import com.telerikacademy.com.springdemo.models.*;
import com.telerikacademy.com.springdemo.repositories.UserRepository;
import com.telerikacademy.com.springdemo.services.CommentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    private final UserRepository userRepository;
    private final CommentReportService reportService;
    @Autowired
    public AdminController(UserRepository userRepository, CommentReportService reportService) {
        this.userRepository = userRepository;
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminDashboard() {
        return ResponseEntity.ok("Welcome to the Admin Dashboard!");
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserListDto>> getAllUsers() {
        List<User> users = userRepository.findAll();

        List<UserListDto> userDtos = users.stream()
                .map(user -> new UserListDto(
                        user.getUserId(),
                        user.getUsername(),
                        user.getPersonalInfo() != null ? user.getPersonalInfo().getEmail() : "N/A"
                ))
                .toList();
        return ResponseEntity.ok(userDtos);
    }
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userRepository.delete(userOptional.get());

        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    @PutMapping("/{reportId}/dismiss")
    public ResponseEntity<ReportCommentResponseDTO> dismissReport(@PathVariable Long reportId, @RequestParam ReportStatus status) {
        ReportCommentResponseDTO dismissed = reportService.dismissReport(reportId, status);
        return ResponseEntity.ok(dismissed);

    }

    @PutMapping("/{reportId}/resolve")
    public ResponseEntity<ReportCommentResponseDTO> resolveReport(@PathVariable Long reportId) {
        ReportCommentResponseDTO resolved = reportService.resolveReport(reportId);
        return ResponseEntity.ok(resolved);
    }
    @PutMapping("/reports/{id}/status")
    public ResponseEntity<ReportCommentResponseDTO> updateReportStatus(
            @PathVariable Long id,
            @RequestBody ReportCommentRequestDTO requestDTO){  // ✅ Enum automatically mapped from string
        ReportCommentResponseDTO updated = reportService.moderateReportedComment(id, requestDTO.getStatus());
        return ResponseEntity.ok((updated));
    }

}