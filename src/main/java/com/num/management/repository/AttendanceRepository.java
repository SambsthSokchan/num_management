package com.num.management.repository;

import com.num.management.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Find attendance records for a specific student, ordered by date (newest
    // first)
    java.util.List<Attendance> findByStudentIdOrderByDateDesc(Long studentId);

    // Fetch the top 5 most recent attendance records globally
    java.util.List<Attendance> findTop5ByOrderByDateDesc();

    // Find all attendance records for a student
    java.util.List<Attendance> findByStudentId(Long studentId);
}
