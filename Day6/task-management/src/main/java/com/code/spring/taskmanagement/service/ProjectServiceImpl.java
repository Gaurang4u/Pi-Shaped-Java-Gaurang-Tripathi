package com.code.spring.taskmanagement.service;


import com.code.spring.taskmanagement.entity.Project;
import com.code.spring.taskmanagement.exception.BadRequestException;
import com.code.spring.taskmanagement.exception.DuplicateResourceException;
import com.code.spring.taskmanagement.exception.ValidationException;
import com.code.spring.taskmanagement.repository.ProjectRepository;
import com.code.spring.taskmanagement.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.code.spring.taskmanagement.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    private final TaskRepository taskRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public Project createProject(Project project) {
        // Validate input
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            throw new ValidationException("Project name cannot be empty.");
        }
        if (project.getDescription() == null || project.getDescription().trim().isEmpty()) {
            throw new ValidationException("Project description cannot be empty.");
        }

        if (projectRepository.existsByName(project.getName())) {
            throw new DuplicateResourceException("Project with name '" + project.getName() + "' already exists.");
        }

        return projectRepository.save(project);
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Optional<Project> getProjectById(Long projectId) {
        return projectRepository.findById(projectId);
    }

    @Override
    public Project updateProject(Long projectId, Project projectDetails) {
        Project project = getProjectById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
        project.setName(projectDetails.getName());
        project.setDescription(projectDetails.getDescription());
        return projectRepository.save(project);
    }

    @Override
    @Transactional
    public void deleteProject(Long projectId) {
        Project project = getProjectById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
        taskRepository.deleteAllByProject(project);
        projectRepository.delete(project);
    }

    @Override
    public List<Project> getProjectsByUser(Long userId) {
        if (userId == null || userId < 1) {
            throw new BadRequestException("Invalid user ID: " + userId);
        }

        List<Project> projects = projectRepository.findByCreatedByUserId(userId);
        if (projects.isEmpty()) {
            throw new ResourceNotFoundException("No projects found for user ID: " + userId);
        }
        return projects;
    }
}
