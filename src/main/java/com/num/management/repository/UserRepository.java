package com.num.management.repository;

import com.num.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Retrieve a user by their email address
    Optional<User> findByEmail(String email);

    // Retrieve users by role name
    java.util.List<User> findByRoles_Name(String name);

    // Count users by role name
    long countByRoles_Name(String name);
}
