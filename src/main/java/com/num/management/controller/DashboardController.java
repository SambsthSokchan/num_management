package com.num.management.controller;

import com.num.management.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private com.num.management.repository.AttendanceRepository attendanceRepository;

    @Autowired
    private com.num.management.repository.ClassRepository classRepository;

    @Autowired
    private com.num.management.repository.StudentRepository studentRepository;

    // Redirect to appropriate dashboard based on Role
    @GetMapping("/")
    public String dashboard(org.springframework.security.core.Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String role = authentication.getAuthorities().stream()
                    .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                    .findFirst().orElse("");

            if ("ROLE_ADMIN".equals(role)) {
                return "redirect:/dashboard/admin";
            } else if ("ROLE_TEACHER".equals(role)) {
                return "redirect:/dashboard/teacher";
            } else if ("ROLE_STUDENT".equals(role)) {
                return "redirect:/dashboard/student";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/dashboard/admin")
    public String adminDashboard(Model model) {
        // Aggregate statistics for the dashboard cards
        model.addAttribute("adminCount", dashboardService.getAdminCount());
        model.addAttribute("teacherCount", dashboardService.getTeacherCount());
        model.addAttribute("studentCount", dashboardService.getStudentCount());
        model.addAttribute("classCount", dashboardService.getClassCount());
        model.addAttribute("role", "ADMIN");

        // Fetch recent activities
        model.addAttribute("recentActivities", attendanceRepository.findTop5ByOrderByDateDesc());

        return "index";
    }

    @GetMapping("/dashboard/teacher")
    public String teacherDashboard(Model model) {
        model.addAttribute("role", "TEACHER");

        // Mock: Get first teacher or one named "Sokha"
        model.addAttribute("teacherName", "Sokha");

        // Mock: Get all classes (In real app, filter by Teacher)
        model.addAttribute("myClasses", classRepository.findAll());

        return "dashboard_teacher";
    }

    @GetMapping("/dashboard/student")
    public String studentDashboard(Model model) {
        model.addAttribute("role", "STUDENT");

        // 1. Find the student (Mock: Find "Dara" or first student)
        com.num.management.model.Student student = studentRepository.findAll().stream()
                .filter(s -> s.getName().contains("Dara"))
                .findFirst()
                .orElse(studentRepository.findAll().stream().findFirst().orElse(null));

        if (student != null) {
            model.addAttribute("studentName", student.getName());
            model.addAttribute("myClass", student.getClassEntity());

            // 2. Calculate Real Attendance Stats
            java.util.List<com.num.management.model.Attendance> records = attendanceRepository
                    .findByStudentId(student.getId());
            if (!records.isEmpty()) {
                long presentCount = records.stream().filter(a -> "Present".equalsIgnoreCase(a.getStatus())).count();
                int percentage = (int) ((presentCount * 100.0) / records.size());
                model.addAttribute("attendanceStats", percentage + "%");
            } else {
                model.addAttribute("attendanceStats", "0%"); // No records
            }
        } else {
            model.addAttribute("studentName", "Guest");
            model.addAttribute("attendanceStats", "0%");
        }

        return "dashboard_student";
    }
}
