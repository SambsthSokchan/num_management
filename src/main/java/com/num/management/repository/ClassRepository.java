package com.num.management.repository;

import com.num.management.model.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    // Basic CRUD operations found in JpaRepository are sufficient here
    boolean existsByName(String name);
}
