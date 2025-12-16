package com.automation.qa.service;

import com.automation.qa.model.User;
import com.automation.qa.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalStateException("Username already exists: " + user.getUsername());
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Email already exists: " + user.getEmail());
        }

        user.setActive(true);
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        return userRepository.findByUsername(username);
    }

    public List<User> getAllActiveUsers() {
        return userRepository.findActiveUsers();
    }

    public User updateUser(Long id, User updatedUser) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        if (updatedUser == null) {
            throw new IllegalArgumentException("Updated user cannot be null");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsername(updatedUser.getUsername())) {
                throw new IllegalStateException("Username already exists: " + updatedUser.getUsername());
            }
            existingUser.setUsername(updatedUser.getUsername());
        }

        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new IllegalStateException("Email already exists: " + updatedUser.getEmail());
            }
            existingUser.setEmail(updatedUser.getEmail());
        }

        return userRepository.save(existingUser);
    }

    public void deactivateUser(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        user.setActive(false);
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        if (!userRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
    }
}
