package com.customer_feedback.pravin.service;

import com.customer_feedback.pravin.dto.FeedbackRequest;
import com.customer_feedback.pravin.dto.Response;
import com.customer_feedback.pravin.model.Feedback;
import com.customer_feedback.pravin.model.User;
import com.customer_feedback.pravin.repository.FeedbackRepository;
import com.customer_feedback.pravin.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FeedbackService {

    private final FeedbackRepository feedback;
    private final UserRepository users;

    public FeedbackService(FeedbackRepository feedback, UserRepository users) {
        this.feedback = feedback;
        this.users = users;
    }

    public List<Map<String, Object>> listForUser(Long userId) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Feedback f : feedback.findByUserOrderByIdDesc(requireUser(userId))) {
            list.add(Response.feedback(f));
        }
        return list;
    }

    public Map<String, Object> createForUser(Long userId, FeedbackRequest req) {
        Feedback f = new Feedback();
        f.setUser(requireUser(userId));
        apply(f, req);
        return Response.feedback(feedback.save(f));
    }

    public Map<String, Object> updateOwn(Long id, Long userId, FeedbackRequest req) {
        Feedback f = ownFeedback(id, userId);
        apply(f, req);
        return Response.feedback(feedback.save(f));
    }

    public void deleteOwn(Long id, Long userId) {
        feedback.delete(ownFeedback(id, userId));
    }

    public List<Map<String, Object>> listAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Feedback f : feedback.findAllByOrderByIdDesc()) {
            list.add(Response.feedback(f));
        }
        return list;
    }

    public Map<String, Object> createForAnyUser(Long userId, FeedbackRequest req) {
        Feedback f = new Feedback();
        f.setUser(requireUser(userId));
        apply(f, req);
        return Response.feedback(feedback.save(f));
    }

    public Map<String, Object> update(Long id, FeedbackRequest req) {
        Feedback f = feedback.findById(id).orElse(null);
        if (f == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Feedback not found.");
        }
        apply(f, req);
        return Response.feedback(feedback.save(f));
    }

    public void delete(Long id) {
        feedback.deleteById(id);
    }

    private User requireUser(Long userId) {
        User u = users.findById(userId).orElse(null);
        if (u == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }
        return u;
    }

    private Feedback ownFeedback(Long id, Long userId) {
        Feedback f = feedback.findById(id).orElse(null);
        if (f == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Feedback not found.");
        }
        if (!f.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only your own feedback can be changed.");
        }
        return f;
    }

    private void apply(Feedback f, FeedbackRequest req) {
        f.setCategory(req.category());
        f.setRating(req.rating());
        f.setMessage(req.message());
    }
}