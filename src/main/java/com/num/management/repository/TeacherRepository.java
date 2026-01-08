package com.num.management.repository;

import com.num.management.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    // Find teachers by name ignoring case with pagination
    org.springframework.data.domain.Page<Teacher> findByNameContainingIgnoreCase(String name,
            org.springframework.data.domain.Pageable pageable);

    // Find all teachers assigned to a specific subject
    java.util.List<Teacher> findBySubjectsId(Long subjectId);
}
