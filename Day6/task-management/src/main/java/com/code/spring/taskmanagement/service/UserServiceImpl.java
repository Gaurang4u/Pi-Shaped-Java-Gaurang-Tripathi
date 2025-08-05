package com.code.spring.taskmanagement.service;

import com.code.spring.taskmanagement.entity.Task;
import com.code.spring.taskmanagement.entity.User;
import com.code.spring.taskmanagement.exception.BadRequestException;
import com.code.spring.taskmanagement.exception.DuplicateResourceException;
import com.code.spring.taskmanagement.exception.ResourceNotFoundException;
import com.code.spring.taskmanagement.repository.TaskRepository;
import com.code.spring.taskmanagement.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final String cacheName = "userCache";

    public UserServiceImpl(UserRepository theUserRepository, TaskRepository theTaskRepository){
        userRepository = theUserRepository;
        taskRepository = theTaskRepository;
    }

    // added implementation for return all Users
    @Cacheable(value = "usersCache", key = "'allUsers'")
    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found in the system.");
        }
        return users;
    }

    @Cacheable(value = cacheName, key = "#userId")
    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId)));
    }

    @CachePut(value = cacheName, key = "#result.userId", unless = "#result == null")
    @Override
    public User createUser(User user) {
        if (user.getUsername() == null || user.getEmail() == null) {
            throw new BadRequestException("Username and Email are required fields.");
        }
        log.info(user.getUsername());
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("A user with email " + user.getEmail() + " already exists.");
        }
        log.info(user.getUsername());
        return userRepository.save(user);
    }

    @CachePut(value = cacheName, key = "#userId")
    @CacheEvict(value = "usersCache", key = "'allUsers'")
    @Override
    public User updateUser(Long userId, User userDetails) {
        if (userId == null || userId < 1) {
            throw new BadRequestException("Invalid user ID: " + userId);
        }

        User user = getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        log.info(String.valueOf(userId));
        if (!user.getEmail().equals(userDetails.getEmail()) && userRepository.existsByEmail(userDetails.getEmail())) {
            throw new DuplicateResourceException("A user with email " + userDetails.getEmail() + " already exists.");
        }
        log.info(String.valueOf(userId));
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        user.setActive(userDetails.getActive());

        return userRepository.save(user);
    }

    @Override
    @CacheEvict(value = {cacheName, "usersCache"}, allEntries = true)
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // unassigning user from all tasks before deleting
        if (user.getTasks() != null && !user.getTasks().isEmpty()) {
            for (Task task : user.getTasks()) {
                task.setAssignedTo(null);
            }
            taskRepository.saveAll(user.getTasks());
        }
        userRepository.delete(user);
    }
}
