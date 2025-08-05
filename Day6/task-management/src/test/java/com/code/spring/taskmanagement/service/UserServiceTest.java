package com.code.spring.taskmanagement.service;

import com.code.spring.taskmanagement.entity.User;
import com.code.spring.taskmanagement.exception.ResourceNotFoundException;
import com.code.spring.taskmanagement.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private List<User> userList;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("user");
        user.setEmail("user@test.com");
        user.setRole("ADMIN");
        user.setActive(true);

        userList = new ArrayList<>();
        userList.add(user);
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(userList);

        List<User> users = userService.getAllUsers();

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
        assertEquals("user", users.getFirst().getUsername());
    }

    @Test
    void testGetAllUsers_NoUsersFound() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.getAllUsers());

        assertEquals("No users found in the system.", exception.getMessage());
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals("user", foundUser.get().getUsername());
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));

        assertEquals("User not found with id: 1", exception.getMessage());
    }

    @Test
    void testCreateUser() {
        when(userRepository.save(user)).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals("user", createdUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/test-users.csv", numLinesToSkip = 1)
    void testCreateUser(String username, String email, String role) {
        User testUser = new User();
        testUser.setUsername(username);
        testUser.setEmail(email);
        testUser.setRole(role);
        testUser.setActive(true);

        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User createdUser = userService.createUser(testUser);

        assertNotNull(createdUser);
        assertEquals(username, createdUser.getUsername());
        assertEquals(email, createdUser.getEmail());
        assertEquals(role, createdUser.getRole());
        log.info(createdUser.getUsername());
    }

    @Test
    void testUpdateUser() {
        User updatedUserDetails = new User();
        updatedUserDetails.setUsername("updatedUser");
        updatedUserDetails.setEmail("updated@example.com");
        updatedUserDetails.setRole("USER");
        updatedUserDetails.setActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUserDetails);

        User updatedUser = userService.updateUser(1L, updatedUserDetails);

        assertEquals("updatedUser", updatedUser.getUsername());
        assertEquals("updated@example.com", updatedUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        User updatedUserDetails = new User();
        updatedUserDetails.setUsername("updatedUser");
        updatedUserDetails.setEmail("updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, updatedUserDetails));

        assertEquals("User not found with id: 1", exception.getMessage());
    }

    @Test
    void testDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));

        assertEquals("User not found with ID: 1", exception.getMessage());
    }
}

