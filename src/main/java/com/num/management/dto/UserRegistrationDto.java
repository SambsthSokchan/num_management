package com.num.management.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    // Role of the user (e.g., "Student", "Teacher", "Admin")
    private String role;
}
