package com.code.spring.taskmanagement.repository;

import com.code.spring.taskmanagement.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comments, Integer> {
}
