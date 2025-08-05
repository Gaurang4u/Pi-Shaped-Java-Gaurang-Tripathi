package com.code.spring.taskmanagement.controller;


import com.code.spring.taskmanagement.entity.Task;
import com.code.spring.taskmanagement.exception.ResourceNotFoundException;
import com.code.spring.taskmanagement.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@Slf4j
public class TaskRestController {

    private final TaskService taskService;

    public TaskRestController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Creates a new task.
     * @param task Task object to create
     * @return ResponseEntity containing the created task
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        log.info("New task created: {}", createdTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    /**
     * Retrieves all tasks.
     * @return ResponseEntity containing the list of tasks
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Retrieves a task by ID.
     * @param taskId ID of the task
     * @return ResponseEntity containing the task or a 404 error if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable("id") Long taskId) {
        Task task = taskService.getTaskById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
        return ResponseEntity.ok(task);
    }

    /**
     * Updates an existing task.
     * @param taskId ID of the task to update
     * @param taskDetails Updated task details
     * @return ResponseEntity containing the updated task
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long taskId, @RequestBody Task taskDetails) {
        Task updatedTask = taskService.updateTask(taskId, taskDetails);
        log.info("Task updated: {}", updatedTask);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Updates the status of an existing task.
     * @param taskId ID of the task to update
     * @param status New status of the task
     * @return ResponseEntity containing the updated task
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable("id") Long taskId, @RequestParam String status) {
        Task updatedTask = taskService.updateTaskStatus(taskId, status);
        log.info("Task status updated: {}", updatedTask);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Deletes a task by ID.
     * @param taskId ID of the task to delete
     * @return ResponseEntity with a success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable("id") Long taskId) {
        taskService.deleteTask(taskId);
        log.info("Task deleted with ID: {}", taskId);
        return ResponseEntity.ok("Task deleted successfully");
    }

    /**
     * Retrieves tasks by status.
     * @param status Status of the tasks
     * @return ResponseEntity containing a list of tasks matching the status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable("status") String status) {
        List<Task> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Retrieves tasks by priority.
     * @param priority Priority of the tasks
     * @return ResponseEntity containing a list of tasks matching the priority
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Task>> getTasksByPriority(@PathVariable("priority") String priority) {
        List<Task> tasks = taskService.getTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Retrieves tasks by project ID.
     * @param projectId ID of the project
     * @return ResponseEntity containing a list of tasks associated with the project
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Task>> getTasksByProject(@PathVariable("projectId") Long projectId) {
        List<Task> tasks = taskService.getTasksByProject(projectId);
        log.info("Fetching tasks for project ID: {}", projectId);
        return ResponseEntity.ok(tasks);
    }
}