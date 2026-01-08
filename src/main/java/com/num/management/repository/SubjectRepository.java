package com.num.management.repository;

import com.num.management.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    // Leverages default JpaRepository methods for database interaction
    // Leverages default JpaRepository methods for database interaction
    org.springframework.data.domain.Page<Subject> findByFaculty(String faculty,
            org.springframework.data.domain.Pageable pageable);
}
