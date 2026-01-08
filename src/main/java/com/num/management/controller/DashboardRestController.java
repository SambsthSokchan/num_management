package com.num.management.controller;

import com.num.management.model.Student;
import com.num.management.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardRestController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private com.num.management.repository.AttendanceRepository attendanceRepository;

    // API endpoint to fetch gender distribution data for charts
    @GetMapping("/chart/gender")
    public List<Integer> getGenderData() {
        // Fetch all students
        List<Student> students = studentRepository.findAll();

        // Count for Male and Female
        long countMale = students.stream()
                .filter(s -> "Male".equalsIgnoreCase(s.getGender()))
                .count();
        long countFemale = students.stream()
                .filter(s -> "Female".equalsIgnoreCase(s.getGender()))
                .count();

        // If no data (and no students seeded yet), just mock some for visual check
        if (students.isEmpty()) {
            return List.of(0, 0);
        }

        return List.of((int) countMale, (int) countFemale);
    }

    // API endpoint to fetch weekly attendance data for charts
    @GetMapping("/chart/attendance")
    public Map<String, List<Integer>> getAttendanceData() {
        // Return structure: { "present": [60,70...], "absent": [50,60...] } for Mon-Fri

        List<Integer> present = new java.util.ArrayList<>();
        List<Integer> absent = new java.util.ArrayList<>();

        // Logic: Iterate Mon(0) to Fri(4) of the current week
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate monday = today.with(java.time.DayOfWeek.MONDAY);

        List<com.num.management.model.Attendance> allAtt = attendanceRepository.findAll();

        for (int i = 0; i < 5; i++) {
            java.time.LocalDate date = monday.plusDays(i);

            // Count present and absent students for this date
            long p = allAtt.stream()
                    .filter(att -> att.getDate().equals(date) && "Present".equals(att.getStatus())).count();
            long a = allAtt.stream()
                    .filter(att -> att.getDate().equals(date) && "Absent".equals(att.getStatus())).count();

            present.add((int) p);
            absent.add((int) a);
        }

        Map<String, List<Integer>> result = new HashMap<>();
        result.put("present", present);
        result.put("absent", absent);

        return result;
    }
}
