package com.num.management.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "classes")
public class ClassEntity {

    // Primary key for the class
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Name of the class (e.g., "Grade 10A")
    private String name;

    // Description or details about the class
    @Column(columnDefinition = "TEXT")
    private String description;

    // Default constructor
    public ClassEntity() {
    }

    // Constructor with fields
    public ClassEntity(String name, String description) {
        this(name, description, null, null);
    }

    // Constructor with all fields
    public ClassEntity(String name, String description, String faculty, String department) {
        this.name = name;
        this.description = description;
        this.faculty = faculty;
        this.department = department;
    }

    // Faculty name (e.g., "Faculty of IT")
    private String faculty;

    // Department name (e.g., "IT", "BIT")
    private String department;

    // Schedule (e.g., "Monday - Friday")
    private String schedule;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    // A class has many subjects, and a subject can be taught in multiple classes
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "class_subjects", joinColumns = @JoinColumn(name = "class_id"), inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private List<Subject> subjects;

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ClassSchedule> schedules;

    public List<ClassSchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<ClassSchedule> schedules) {
        this.schedules = schedules;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }
}
