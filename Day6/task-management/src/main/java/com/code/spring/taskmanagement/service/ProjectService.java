package com.code.spring.taskmanagement.service;


import com.code.spring.taskmanagement.entity.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    Project createProject(Project project);
    List<Project> getAllProjects();
    Optional<Project> getProjectById(Long projectId); // Return Optional
    Project updateProject(Long projectId, Project projectDetails);
    void deleteProject(Long projectId);
    List<Project> getProjectsByUser(Long userId); // Return Optional
}
