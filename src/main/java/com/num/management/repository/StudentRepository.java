package com.num.management.repository;

import com.num.management.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    java.util.List<Student> findByNameContainingIgnoreCase(String name);

    java.util.List<Student> findByClassEntityId(Long classId);
}
