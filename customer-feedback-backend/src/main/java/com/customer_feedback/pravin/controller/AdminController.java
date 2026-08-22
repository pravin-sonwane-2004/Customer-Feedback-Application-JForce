package com.customer_feedback.pravin.controller;

import com.customer_feedback.pravin.dto.FeedbackRequest;
import com.customer_feedback.pravin.service.AuthService;
import com.customer_feedback.pravin.service.FeedbackService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final FeedbackService feedbackService;
    private final AuthService authService;

    public AdminController(FeedbackService feedbackService, AuthService authService) {
        this.feedbackService = feedbackService;
        this.authService = authService;
    }

    @GetMapping("/feedback")
    public List<Map<String, Object>> allFeedback() {
        return feedbackService.listAll();
    }

    @PostMapping("/feedback")
    public Map<String, Object> create(@RequestParam Long userId, @RequestBody FeedbackRequest req) {
        return feedbackService.createForAnyUser(userId, req);
    }

    @PutMapping("/feedback/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody FeedbackRequest req) {
        return feedbackService.update(id, req);
    }

    @DeleteMapping("/feedback/{id}")
    public void delete(@PathVariable Long id) {
        feedbackService.delete(id);
    }

    @GetMapping("/users")
    public List<Map<String, Object>> allUsers() {
        return authService.listUsers();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        authService.deleteUser(id);
    }
}