package com.customer_feedback.pravin.service;

import com.customer_feedback.pravin.dto.AuthRequest;
import com.customer_feedback.pravin.dto.Response;
import com.customer_feedback.pravin.model.User;
import com.customer_feedback.pravin.repository.FeedbackRepository;
import com.customer_feedback.pravin.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin123";

    private final UserRepository users;
    private final FeedbackRepository feedback;

    public AuthService(UserRepository users, FeedbackRepository feedback) {
        this.users = users;
        this.feedback = feedback;
    }

    public Map<String, Object> register(AuthRequest req) {
        if (users.findByEmail(req.email()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered.");
        }
        User u = new User();
        u.setName(req.name());
        u.setEmail(req.email());
        u.setPassword(req.password());
        u.setRole("USER");
        return Response.user(users.save(u));
    }

    public Map<String, Object> login(AuthRequest req) {
        if (ADMIN_EMAIL.equals(req.email()) && ADMIN_PASSWORD.equals(req.password())) {
            Map<String, Object> admin = new LinkedHashMap<>();
            admin.put("id", 0L);
            admin.put("name", "Administrator");
            admin.put("email", ADMIN_EMAIL);
            admin.put("role", "ADMIN");
            return admin;
        }
        User u = users.findByEmail(req.email());
        if (u == null || !u.getPassword().equals(req.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }
        return Response.user(u);
    }

    public List<Map<String, Object>> listUsers() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (User u : users.findAll()) {
            list.add(Response.user(u));
        }
        return list;
    }

    public void deleteUser(Long userId) {
        feedback.deleteByUserId(userId);
        users.deleteById(userId);
    }
}