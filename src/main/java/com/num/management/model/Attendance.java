package com.num.management.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "attendance")
@Data
@NoArgsConstructor
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The student this attendance record belongs to
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    // Date of the attendance record
    private LocalDate date;

    // Status of attendance: "Present", "Absent", "Late", etc.
    private String status;
}
