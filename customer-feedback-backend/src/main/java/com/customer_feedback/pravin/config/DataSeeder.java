package com.customer_feedback.pravin.config;

import com.customer_feedback.pravin.model.User;
import com.customer_feedback.pravin.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Seeds a default ADMIN account on startup so the application has an
 * administrator without relying on hard-coded string comparisons.
 */
@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedAdmin(UserRepository users, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@gmail.com";
            if (users.findByEmail(adminEmail) == null) {
                User admin = new User();
                admin.setName("Administrator");
                admin.setUsername("admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ADMIN");
                users.save(admin);
            }
        };
    }
}