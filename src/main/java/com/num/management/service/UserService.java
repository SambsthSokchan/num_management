package com.num.management.service;

import com.num.management.dto.UserRegistrationDto;
import com.num.management.model.Role;
import com.num.management.model.User;
import com.num.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    @Lazy // Avoid circular dependency if SecurityConfig uses this
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User save(UserRegistrationDto registrationDto) {
        User user = new User();
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setEmail(registrationDto.getEmail());
        // Configure password encoding in SecurityConfig and inject here if needed, or
        // simple pass
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
            }
        }
        user.setRoles(Arrays.asList(new Role(roleName)));

        return userRepository.save(user);
    }
}
