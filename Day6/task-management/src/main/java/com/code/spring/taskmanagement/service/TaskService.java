package com.code.spring.taskmanagement.service;


import com.code.spring.taskmanagement.entity.Task;
import java.util.List;
import java.util.Optional;

public interface TaskService {
    Task createTask(Task task);
    List<Task> getAllTasks();
    Optional<Task> getTaskById(Long taskId);
    Task updateTask(Long taskId, Task taskDetails);
    void deleteTask(Long taskId);
    Task updateTaskStatus(Long taskId, String status);
    List<Task> getTasksByStatus(String status);
    List<Task> getTasksByPriority(String priority);
    List<Task> getTasksByProject(Long projectId);

}
