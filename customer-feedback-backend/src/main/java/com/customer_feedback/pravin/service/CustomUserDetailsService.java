package com.customer_feedback.pravin.service;

import com.customer_feedback.pravin.model.User;
import com.customer_feedback.pravin.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Bridges the existing {@link User} entity to Spring Security. The unique
 * login identifier is the email address (the existing product behaviour);
 * the username column is accepted as a secondary identifier.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    public CustomUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = users.findByEmail(login);
        if (user == null) {
            user = users.findByUsername(login);
        }
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + login);
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}