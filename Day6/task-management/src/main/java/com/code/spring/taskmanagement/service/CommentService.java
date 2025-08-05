package com.code.spring.taskmanagement.service;

import com.code.spring.taskmanagement.entity.Comments;
import com.code.spring.taskmanagement.entity.User;
import com.code.spring.taskmanagement.exception.ResourceNotFoundException;
import com.code.spring.taskmanagement.repository.CommentRepository;
import com.code.spring.taskmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public CommentService(UserRepository theUserRepository, CommentRepository theCommentRepository){
        userRepository = theUserRepository;
        commentRepository = theCommentRepository;
    }

    public Comments addComment(Long userId, String review){
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            Comments comments = new Comments();
            comments.setReview(review.trim());
            comments.setUser(user);
            return commentRepository.save(comments);
        }
        throw new ResourceNotFoundException("No projects found for user ID: " + userId);
    }

    public List<Comments> getCommentsByUser(Long userId) {
        return commentRepository.findAll().stream()
                .filter(comment -> comment.getUser().getUserId().equals(userId))
                .toList();
    }
}
