package com.code.spring.taskmanagement.repository;

import com.code.spring.taskmanagement.entity.Project;
import com.code.spring.taskmanagement.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(String status);
    List<Task> findByPriority(String priority);
    List<Task> findByProjectProjectId(Long projectId);
    void deleteAllByProject(Project project);
}
