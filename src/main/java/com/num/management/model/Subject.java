package com.num.management.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subjects")
@Data
@NoArgsConstructor
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Name of the subject (e.g., "Mathematics")
    private String name;

    // Unique subject code (e.g., "MATH101")
    private String code;

    // Description of the subject curriculum
    // Description of the subject curriculum
    private String description;

    // Faculty this subject belongs to
    private String faculty;

    public Subject(String name, String code, String description) {
        this(name, code, description, "Unassigned");
    }

    public Subject(String name, String code, String description, String faculty) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.faculty = faculty;
    }
}
