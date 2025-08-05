package com.code.spring.taskmanagement.service;

import com.code.spring.taskmanagement.entity.Task;
import com.code.spring.taskmanagement.exception.DuplicateResourceException;
import com.code.spring.taskmanagement.exception.ResourceNotFoundException;
import com.code.spring.taskmanagement.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public Task createTask(Task task) {
        if (task.getTaskId() != null && taskRepository.existsById(task.getTaskId())) {
            throw new DuplicateResourceException("Task with ID " + task.getTaskId() + " already exists");
        }
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Optional<Task> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    @Override
    public Task updateTask(Long taskId, Task taskDetails) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        task.setTitle(taskDetails.getTitle());
        task.setStatus(taskDetails.getStatus());
        task.setPriority(taskDetails.getPriority());
        task.setDeadline(taskDetails.getDeadline());
        task.setAssignedTo(taskDetails.getAssignedTo());

        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
        taskRepository.delete(task);
    }

    @Override
    public Task updateTaskStatus(Long taskId, String status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
        task.setStatus(status);
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getTasksByStatus(String status) {
        List<Task> tasks = taskRepository.findByStatus(status);
        if (tasks.isEmpty()) {
            throw new ResourceNotFoundException("No tasks found with status: " + status);
        }
        return tasks;
    }

    @Override
    public List<Task> getTasksByPriority(String priority) {
        List<Task> tasks = taskRepository.findByPriority(priority);
        if (tasks.isEmpty()) {
            throw new ResourceNotFoundException("No tasks found with priority: " + priority);
        }
        return tasks;
    }

    @Override
    public List<Task> getTasksByProject(Long projectId) {
        List<Task> tasks = taskRepository.findByProjectProjectId(projectId);
        if (tasks.isEmpty()) {
            throw new ResourceNotFoundException("No tasks found for project ID: " + projectId);
        }
        return tasks;
    }
}

