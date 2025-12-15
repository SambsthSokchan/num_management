package com.num.management.repository;

import com.num.management.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    java.util.List<Teacher> findByNameContainingIgnoreCase(String name);

    java.util.List<Teacher> findBySubjectEntityId(Long subjectId);
}
