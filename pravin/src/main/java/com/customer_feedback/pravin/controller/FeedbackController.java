package com.customer_feedback.pravin.controller;

import com.customer_feedback.pravin.dto.FeedbackRequest;
import com.customer_feedback.pravin.service.FeedbackService;
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
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/{userId}")
    public List<Map<String, Object>> listForUser(@PathVariable Long userId) {
        return feedbackService.listForUser(userId);
    }

    @PostMapping
    public Map<String, Object> create(@RequestParam Long userId, @RequestBody FeedbackRequest req) {
        return feedbackService.createForUser(userId, req);
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestParam Long userId,
                                      @RequestBody FeedbackRequest req) {
        return feedbackService.updateOwn(id, userId, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestParam Long userId) {
        feedbackService.deleteOwn(id, userId);
    }
}