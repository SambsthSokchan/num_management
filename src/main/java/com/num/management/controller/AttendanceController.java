package com.num.management.controller;

import com.num.management.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AttendanceController {

    @Autowired
    private com.num.management.repository.StudentRepository studentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @GetMapping("/attendance")
    public String listAttendance(Model model) {
        model.addAttribute("attendances", attendanceRepository.findAll());
        return "attendance";
    }

    @GetMapping("/attendance/new")
    public String showAddAttendanceForm(Model model) {
        com.num.management.model.Attendance attendance = new com.num.management.model.Attendance();
        model.addAttribute("attendance", attendance);
        model.addAttribute("students", studentRepository.findAll());
        return "attendance_form";
    }

    @org.springframework.web.bind.annotation.PostMapping("/attendance/save")
    public String saveAttendance(
            @org.springframework.web.bind.annotation.ModelAttribute("attendance") com.num.management.model.Attendance attendance) {
        attendanceRepository.save(attendance);
        return "redirect:/attendance";
    }

    @GetMapping("/attendance/edit/{id}")
    public String showEditForm(@org.springframework.web.bind.annotation.PathVariable("id") Long id, Model model) {
        com.num.management.model.Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid attendance Id:" + id));
        model.addAttribute("attendance", attendance);
        model.addAttribute("students", studentRepository.findAll());
        return "attendance_form";
    }

    @org.springframework.web.bind.annotation.PostMapping("/attendance/update/{id}")
    public String updateAttendance(@org.springframework.web.bind.annotation.PathVariable("id") Long id,
            @org.springframework.web.bind.annotation.ModelAttribute("attendance") com.num.management.model.Attendance attendance,
            Model model) {
        attendance.setId(id);
        attendanceRepository.save(attendance);
        return "redirect:/attendance";
    }

    @GetMapping("/attendance/delete/{id}")
    public String deleteAttendance(@org.springframework.web.bind.annotation.PathVariable("id") Long id) {
        com.num.management.model.Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid attendance Id:" + id));
        attendanceRepository.delete(attendance);
        return "redirect:/attendance";
    }
}
