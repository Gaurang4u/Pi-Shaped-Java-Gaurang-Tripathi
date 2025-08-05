package com.code.spring.taskmanagement.controller;

import com.code.spring.taskmanagement.entity.Comments;
import com.code.spring.taskmanagement.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentRestController {
    private final CommentService commentService;

    public CommentRestController (CommentService theCommentService){
        commentService = theCommentService;
    }

    @PostMapping("/add/{userId}")
    public ResponseEntity<Comments> addComment(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        String review = request.get("review");
        Comments newComment = commentService.addComment(userId, review);
        return ResponseEntity.ok(newComment);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Comments>> getCommentsByUser(@PathVariable Long userId) {
        List<Comments> comments = commentService.getCommentsByUser(userId);
        return ResponseEntity.ok(comments);
    }
}
