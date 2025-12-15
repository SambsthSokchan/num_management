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

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("adminCount", dashboardService.getAdminCount());
        model.addAttribute("teacherCount", dashboardService.getTeacherCount());
        model.addAttribute("studentCount", dashboardService.getStudentCount());
        model.addAttribute("classCount", dashboardService.getClassCount());

        // Recent Activities
        model.addAttribute("recentActivities", attendanceRepository.findTop5ByOrderByDateDesc());

        return "index"; // The MAIN DASHBOARD
    }
}
