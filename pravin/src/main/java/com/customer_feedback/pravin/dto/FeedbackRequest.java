package com.customer_feedback.pravin.dto;

public record FeedbackRequest(String category, Integer rating, String message) {
}