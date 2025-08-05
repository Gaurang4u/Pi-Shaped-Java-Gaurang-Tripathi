package com.code.spring.taskmanagement.controller;

import com.code.spring.taskmanagement.entity.Project;
import com.code.spring.taskmanagement.exception.ResourceNotFoundException;
import com.code.spring.taskmanagement.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/projects")
@Slf4j
public class ProjectRestController {

    private final ProjectService projectService;

    public ProjectRestController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Creates a new project.
     * @param project Project object to create
     * @return ResponseEntity containing the created project
     */
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        Project createdProject = projectService.createProject(project);
        log.info("New project created: {}", createdProject);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    /**
     * Retrieves all projects.
     * @return ResponseEntity containing the list of projects
     */
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Retrieves a project by ID.
     * @param projectId ID of the project
     * @return ResponseEntity containing the project or a 404 error if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable("id") Long projectId) {
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
        return ResponseEntity.ok(project);
    }

    /**
     * Updates an existing project.
     * @param projectId ID of the project to update
     * @param projectDetails Updated project details
     * @return ResponseEntity containing the updated project
     */
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable("id") Long projectId, @RequestBody Project projectDetails) {
        Project updatedProject = projectService.updateProject(projectId, projectDetails);
        log.info("Project updated: {}", updatedProject);
        return ResponseEntity.ok(updatedProject);
    }

    /**
     * Deletes a project by ID.
     * @param projectId ID of the project to delete
     * @return ResponseEntity with a success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable("id") Long projectId) {
        projectService.deleteProject(projectId);
        log.info("Project deleted with ID: {}", projectId);
        return ResponseEntity.ok("Project with ID " + projectId + " has been deleted successfully.");
    }

    /**
     * Retrieves projects by user ID.
     * @param userId ID of the user
     * @return ResponseEntity containing a list of projects associated with the user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Project>> getProjectsByUser(@PathVariable("userId") Long userId) {
        List<Project> projects = projectService.getProjectsByUser(userId);
        return ResponseEntity.ok(projects);
    }
}
