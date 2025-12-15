package com.num.management.controller;

import com.num.management.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private com.num.management.repository.ClassRepository classRepository;

    @GetMapping("/students")
    public String listStudents(Model model,
            @org.springframework.web.bind.annotation.RequestParam(value = "keyword", required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("students", studentRepository.findByNameContainingIgnoreCase(keyword));
        } else {
            model.addAttribute("students", studentRepository.findAll());
        }
        model.addAttribute("keyword", keyword); // Keep the keyword in the search box
        return "students";
    }

    @GetMapping("/students/new")
    public String showNewStudentForm(Model model) {
        com.num.management.model.Student student = new com.num.management.model.Student();
        model.addAttribute("student", student);
        model.addAttribute("classes", classRepository.findAll());
        return "student_form";
    }

    @org.springframework.web.bind.annotation.PostMapping("/students/save")
    public String saveStudent(
            @jakarta.validation.Valid @org.springframework.web.bind.annotation.ModelAttribute("student") com.num.management.model.Student student,
            org.springframework.validation.BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("classes", classRepository.findAll());
            return "student_form";
        }
        studentRepository.save(student);
        return "redirect:/students";
    }

    @GetMapping("/students/edit/{id}")
    public String showEditForm(@org.springframework.web.bind.annotation.PathVariable("id") Long id, Model model) {
        com.num.management.model.Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));
        model.addAttribute("student", student);
        model.addAttribute("classes", classRepository.findAll()); // Fetch all classes
        return "student_form";
    }

    @org.springframework.web.bind.annotation.PostMapping("/students/update/{id}")
    public String updateStudent(@org.springframework.web.bind.annotation.PathVariable("id") Long id,
            @jakarta.validation.Valid @org.springframework.web.bind.annotation.ModelAttribute("student") com.num.management.model.Student student,
            org.springframework.validation.BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            student.setId(id); // Keep ID
            model.addAttribute("classes", classRepository.findAll());
            return "student_form";
        }
        student.setId(id);
        studentRepository.save(student);
        return "redirect:/students";
    }

    @Autowired
    private com.num.management.repository.AttendanceRepository attendanceRepository;

    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@org.springframework.web.bind.annotation.PathVariable("id") Long id) {
        com.num.management.model.Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));

        // Manually delete related attendance records first to avoid foreign key
        // constraint error
        java.util.List<com.num.management.model.Attendance> attendanceRecords = attendanceRepository
                .findByStudentIdOrderByDateDesc(id);
        attendanceRepository.deleteAll(attendanceRecords);

        studentRepository.delete(student);
        return "redirect:/students";
    }

    @GetMapping("/students/{id}")
    public String viewStudent(@org.springframework.web.bind.annotation.PathVariable("id") Long id, Model model) {
        com.num.management.model.Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));

        java.util.List<com.num.management.model.Attendance> attendanceList = attendanceRepository
                .findByStudentIdOrderByDateDesc(id);

        // Calculate Stats
        long total = attendanceList.size();
        long present = attendanceList.stream().filter(a -> "Present".equalsIgnoreCase(a.getStatus())).count();
        double rate = total > 0 ? (double) present / total * 100 : 0.0;

        model.addAttribute("student", student);
        model.addAttribute("attendanceList", attendanceList);
        model.addAttribute("attendanceRate", String.format("%.1f", rate));

        return "student_details";
    }
}
