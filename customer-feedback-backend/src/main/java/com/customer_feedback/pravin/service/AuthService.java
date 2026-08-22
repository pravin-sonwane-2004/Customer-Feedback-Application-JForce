package com.customer_feedback.pravin.service;

import com.customer_feedback.pravin.dto.AuthRequest;
import com.customer_feedback.pravin.dto.Response;
import com.customer_feedback.pravin.model.User;
import com.customer_feedback.pravin.repository.FeedbackRepository;
import com.customer_feedback.pravin.repository.UserRepository;
import com.customer_feedback.pravin.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository users;
    private final FeedbackRepository feedback;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository users,
                       FeedbackRepository feedback,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.users = users;
        this.feedback = feedback;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public Map<String, Object> register(AuthRequest req) {
        String name = req.name() == null ? "" : req.name().trim();
        String email = req.email() == null ? "" : req.email().trim();
        String password = req.password();

        if (name.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required.");
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid email is required.");
        }
        if (password == null || password.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters.");
        }
        if (users.findByEmail(email) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered.");
        }
        if (users.findByUsername(name) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken.");
        }

        User user = new User();
        user.setName(name);
        user.setUsername(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        return Response.user(users.save(user));
    }

    public Map<String, Object> login(AuthRequest req) {
        String email = req.email() == null ? "" : req.email().trim();
        String password = req.password() == null ? "" : req.password();

        if (email.isEmpty() || password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and password are required.");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        User user = users.findByEmail(principal.getUsername());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }

        String token = jwtService.generateToken(principal);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", token);
        result.putAll(Response.user(user));
        return result;
    }

    public Map<String, Object> me() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = users.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");
        }
        return Response.user(user);
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