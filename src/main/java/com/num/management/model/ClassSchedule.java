package com.num.management.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "class_schedules")
@Data
@NoArgsConstructor
public class ClassSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dayOfWeek; // Mon, Tue, Wed, Thu, Fri

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;

    public ClassSchedule(String dayOfWeek, Subject subject, ClassEntity classEntity) {
        this.dayOfWeek = dayOfWeek;
        this.subject = subject;
        this.classEntity = classEntity;
    }
}
