package com.customer_feedback.pravin.dto;

import com.customer_feedback.pravin.model.Feedback;
import com.customer_feedback.pravin.model.User;

import java.util.LinkedHashMap;
import java.util.Map;

/** Tiny helpers that turn entities into clean JSON maps. */
public final class Response {

    private Response() {
    }

    public static Map<String, Object> user(User u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", u.getId());
        m.put("name", u.getName());
        m.put("email", u.getEmail());
        m.put("role", u.getRole());
        return m;
    }

    public static Map<String, Object> feedback(Feedback f) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", f.getId());
        m.put("category", f.getCategory());
        m.put("rating", f.getRating());
        m.put("message", f.getMessage());
        m.put("userId", f.getUser().getId());
        m.put("userName", f.getUser().getName());
        m.put("userEmail", f.getUser().getEmail());
        return m;
    }
}