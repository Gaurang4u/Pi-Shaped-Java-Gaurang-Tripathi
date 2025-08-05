package com.code.spring.taskmanagement.controller;


import com.code.spring.taskmanagement.entity.User;
import com.code.spring.taskmanagement.exception.ResourceNotFoundException;
import com.code.spring.taskmanagement.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserRestController {

    private UserService userService;

    public UserRestController(UserService theUserService){
        userService = theUserService;
    }

    /**
     * Retrieves all users in the application.
     * @return List of Users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long userId) { // Use Long
        log.info("Fetching user with ID: {}", userId);

        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });
    }

    /**
     * Creates a new user in the system.
     * @param user User object to create
     * @return ResponseEntity containing the created user
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.createUser(user);
        log.info("New user created: {}", savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }


    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long userId, @RequestBody User userDetails) {
        return ResponseEntity.ok(userService.updateUser(userId, userDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User with ID " + userId + " deleted successfully");
    }

}
