package com.code.spring.taskmanagement.repository;

import com.code.spring.taskmanagement.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByCreatedByUserId(Long userId);

    boolean existsByName(String name);
}
