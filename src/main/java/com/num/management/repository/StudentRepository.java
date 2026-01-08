package com.num.management.repository;

import com.num.management.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

        // Case-insensitive search for students by name
        // Case-insensitive search for students by name with pagination
        org.springframework.data.domain.Page<Student> findByNameContainingIgnoreCase(String name,
                        org.springframework.data.domain.Pageable pageable);

        // Find all students belonging to a specific class with pagination
        org.springframework.data.domain.Page<Student> findByClassEntityId(Long classId,
                        org.springframework.data.domain.Pageable pageable);

        // Count students in a class
        long countByClassEntityId(Long classId);
}
