package com.code.spring.taskmanagement.service;

import com.code.spring.taskmanagement.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long userId);
    User createUser(User user);
    User updateUser(Long userId, User userDetails);
    void deleteUser(Long userId);

}
