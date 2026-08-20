package com.customer_feedback.pravin.repository;

import com.customer_feedback.pravin.model.Feedback;
import com.customer_feedback.pravin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByUserOrderByIdDesc(User user);

    List<Feedback> findAllByOrderByIdDesc();

    void deleteByUserId(Long userId);
}