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

    @Autowired
    private com.num.management.repository.ClassRepository classRepository;

    // --- Marking Attendance Flow ---

    // 1. Select Class to mark
    @GetMapping("/attendance/mark")
    public String showMarkAttendanceClassSelection(Model model) {
        model.addAttribute("classes", classRepository.findAll());
        return "attendance_mark_select"; // We can reuse "attendance_mark" if we handle null classId
    }

    // 2. Show Student List for marking
    @GetMapping("/attendance/mark/{classId}")
    public String showMarkAttendanceForm(@org.springframework.web.bind.annotation.PathVariable("classId") Long classId,
            Model model) {
        if (classId == null) {
            throw new IllegalArgumentException("Class ID cannot be null");
        }
        com.num.management.model.ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid class Id:" + classId));

        java.util.List<com.num.management.model.Student> students = studentRepository
                .findByClassEntityId(classId, org.springframework.data.domain.Pageable.unpaged()).getContent();

        AttendanceWrapper form = new AttendanceWrapper();
        java.util.List<com.num.management.model.Attendance> attendanceList = new java.util.ArrayList<>();

        for (com.num.management.model.Student s : students) {
            com.num.management.model.Attendance a = new com.num.management.model.Attendance();
            a.setStudent(s);
            a.setDate(java.time.LocalDate.now());
            a.setStatus("Present"); // Default
            attendanceList.add(a);
        }
        form.setAttendanceList(attendanceList);

        model.addAttribute("classEntity", classEntity);
        model.addAttribute("form", form);
        return "attendance_mark";
    }

    // 3. Save Bulk Attendance
    @org.springframework.web.bind.annotation.PostMapping("/attendance/mark/save")
    public String saveBulkAttendance(
            @org.springframework.web.bind.annotation.ModelAttribute("form") AttendanceWrapper form) {
        if (form.getAttendanceList() != null) {
            for (com.num.management.model.Attendance a : form.getAttendanceList()) {
                // Ensure date is set if lost (though hidden field should keep it)
                if (a.getDate() == null)
                    a.setDate(java.time.LocalDate.now());
                attendanceRepository.save(a);
            }
        }
        return "redirect:/attendance";
    }

    // Wrapper for list binding
    public static class AttendanceWrapper {
        private java.util.List<com.num.management.model.Attendance> attendanceList;

        public java.util.List<com.num.management.model.Attendance> getAttendanceList() {
            return attendanceList;
        }

        public void setAttendanceList(java.util.List<com.num.management.model.Attendance> attendanceList) {
            this.attendanceList = attendanceList;
        }
    }

    // Display the list of all attendance records with pagination
    @GetMapping("/attendance")
    public String listAttendance(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size,
            Model model) {
        // Create Pageable instance for pagination and sorting
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by("date").descending());

        org.springframework.data.domain.Page<com.num.management.model.Attendance> attendancePage = attendanceRepository
                .findAll(pageable);

        model.addAttribute("attendances", attendancePage);

        // Pagination Logic for "Gap" style (1 ... 5 6 7 ... 10)
        int totalPages = attendancePage.getTotalPages();
        if (totalPages > 0) {
            java.util.List<Integer> pageNumbers = new java.util.ArrayList<>();
            int pageNumber = attendancePage.getNumber() + 1; // 1-based current page

            // Always add first page
            pageNumbers.add(1);

            // Add middle pages
            for (int i = 2; i < totalPages; i++) {
                // Show if it's within distance of 2 from current page OR if it's 2nd or
                // 2nd-to-last
                if (i == 2 || i == totalPages - 1 || Math.abs(i - pageNumber) <= 1) {
                    pageNumbers.add(i);
                } else if (pageNumbers.get(pageNumbers.size() - 1) != -1) {
                    // Add gap marker if not already present
                    pageNumbers.add(-1);
                }
            }

            // Always add last page if greater than 1
            if (totalPages > 1) {
                pageNumbers.add(totalPages);
            }
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "attendance";
    }

    // Show the form for creating a new attendance record
    @GetMapping("/attendance/new")
    public String showAddAttendanceForm(Model model) {
        // Create a new Attendance object to hold form data
        com.num.management.model.Attendance attendance = new com.num.management.model.Attendance();
        model.addAttribute("attendance", attendance);
        // Load all students to populate student selection in the form
        model.addAttribute("students", studentRepository.findAll());
        return "attendance_form";
    }

    // Save the new or updated attendance record to the database
    @org.springframework.web.bind.annotation.PostMapping("/attendance/save")
    public String saveAttendance(
            @org.springframework.web.bind.annotation.ModelAttribute("attendance") com.num.management.model.Attendance attendance) {
        // Save the attendance entity using the repository
        attendanceRepository.save(attendance);
        return "redirect:/attendance"; // Redirect back to the list page
    }

    // Display the form for editing an existing attendance record
    @GetMapping("/attendance/edit/{id}")
    public String showEditForm(@org.springframework.web.bind.annotation.PathVariable("id") Long id, Model model) {
        // Find the attendance record by ID, throw exception if not found
        com.num.management.model.Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid attendance Id:" + id));
        model.addAttribute("attendance", attendance);
        // Load students for the form dropdown
        model.addAttribute("students", studentRepository.findAll());
        return "attendance_form";
    }

    // Update an existing attendance record
    @org.springframework.web.bind.annotation.PostMapping("/attendance/update/{id}")
    public String updateAttendance(@org.springframework.web.bind.annotation.PathVariable("id") Long id,
            @org.springframework.web.bind.annotation.ModelAttribute("attendance") com.num.management.model.Attendance attendance,
            Model model) {
        // Ensure the ID is set on the object so it updates instead of creates new
        attendance.setId(id);
        attendanceRepository.save(attendance);
        return "redirect:/attendance";
    }

    // Delete an attendance record by ID
    @GetMapping("/attendance/delete/{id}")
    public String deleteAttendance(@org.springframework.web.bind.annotation.PathVariable("id") Long id) {
        // Find the attendance to ensure it exists before deleting
        com.num.management.model.Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid attendance Id:" + id));
        attendanceRepository.delete(attendance);
        return "redirect:/attendance";
    }
}
