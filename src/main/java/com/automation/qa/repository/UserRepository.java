package com.automation.qa.repository;

import com.automation.qa.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    List<User> findActiveUsers();

    void deleteById(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
