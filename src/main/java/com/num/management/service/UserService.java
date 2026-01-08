package com.num.management.service;

import com.num.management.dto.UserRegistrationDto;
import com.num.management.model.Role;
import com.num.management.model.User;
import com.num.management.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    @Lazy // Avoid circular dependency if SecurityConfig uses this
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user account in the system.
     * <p>
     * This method handles the creation of a new {@link User} entity from the
     * provided
     * {@link UserRegistrationDto}. It performs the following steps:
     * <ul>
     * <li>Maps DTO fields (firstName, lastName, email) to the User entity.</li>
     * <li>Encodes the raw password using {@link PasswordEncoder}.</li>
     * <li>Assigns the appropriate {@link Role} based on the selection.</li>
     * <li>Saves the user to the database via {@link UserRepository}.</li>
     * </ul>
     * </p>
     *
     * @param registrationDto Data Transfer Object containing user registration
     *                        details.
     * @return The saved {@link User} entity.
     */
    public User save(UserRegistrationDto registrationDto) {
        log.info("Attempting to register new user with email: {}", registrationDto.getEmail());

        User user = new User();
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setEmail(registrationDto.getEmail());

        // Encode the password before saving for security
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        // Assign Role based on selection "Admin", "Teacher", "Student"
        String roleName = "ROLE_STUDENT"; // Default
        if (registrationDto.getRole() != null) {
            switch (registrationDto.getRole()) {
                case "Admin":
                    roleName = "ROLE_ADMIN";
                    break;
                case "Teacher":
                    roleName = "ROLE_TEACHER";
                    break;
                case "Student":
                    roleName = "ROLE_STUDENT";
                    break;
                default:
                    log.warn("Unknown role '{}' provided. Defaulting to ROLE_STUDENT.", registrationDto.getRole());
            }
        }
        user.setRoles(Arrays.asList(new Role(roleName)));

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {} and Role: {}", savedUser.getId(), roleName);
        return savedUser;
    }
}
