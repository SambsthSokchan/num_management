package com.num.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
public class Student {

    // Primary key for the student entity
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Student's full name, cannot be blank and must be at least 2 characters
    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, message = "Name must be at least 2 characters")
    private String name;

    // Student's email, must be a valid email format
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;

    // Relationship: Many students belong to one class
    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;

    private String phoneNumber;
    private String gender; // "Male" or "Female"

    // Helper method to safely get the class name for display in views (e.g.,
    // Thymeleaf)
    public String getClassName() {
        return classEntity != null ? classEntity.getName() : "N/A";
    }
}
