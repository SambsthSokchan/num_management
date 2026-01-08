package com.num.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teachers")
@Data
@NoArgsConstructor
public class Teacher {

    // Primary key for the teacher entity
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Teacher's name, mandatory and at least 2 characters
    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, message = "Name must be at least 2 characters")
    private String name;

    // Teacher's email address, mandatory and valid email format
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;

    // Relationship: A teacher can specialize in multiple subjects
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "teacher_subjects", joinColumns = @JoinColumn(name = "teacher_id"), inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private java.util.Set<Subject> subjects = new java.util.HashSet<>();

    private String phoneNumber;

    // Helper method to safely retrieve the subject names for display
    public String getSubjectName() {
        if (subjects == null || subjects.isEmpty()) {
            return "N/A";
        }
        return subjects.stream()
                .map(Subject::getName)
                .collect(java.util.stream.Collectors.joining(", "));
    }
}
