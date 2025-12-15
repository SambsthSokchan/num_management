package com.num.management.repository;

import com.num.management.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    java.util.List<Attendance> findByStudentIdOrderByDateDesc(Long studentId);

    java.util.List<Attendance> findTop5ByOrderByDateDesc();
}
