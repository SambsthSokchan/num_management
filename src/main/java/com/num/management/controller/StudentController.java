package com.num.management.controller;

import com.num.management.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private com.num.management.repository.ClassRepository classRepository;

    /**
     * Displays a list of students, optionally filtered by a search keyword or
     * class.
     *
     * @param model   The UI model to hold attributes.
     * @param keyword Optional search term for filtering students by name.
     * @param classId Optional class ID for filtering students by class.
     * @return The "students" view name.
     */
    @GetMapping("/students")
    public String listStudents(Model model,
            @org.springframework.web.bind.annotation.RequestParam(value = "keyword", required = false) String keyword,
            @org.springframework.web.bind.annotation.RequestParam(value = "classId", required = false) Long classId,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size) {

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by("name"));
        org.springframework.data.domain.Page<com.num.management.model.Student> studentPage;

        if (classId != null) {
            log.debug("Filtering students by class ID: {}", classId);
            studentPage = studentRepository.findByClassEntityId(classId, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            log.debug("Searching for students with keyword: {}", keyword);
            studentPage = studentRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            log.debug("Fetching all students list");
            studentPage = studentRepository.findAll(pageable);
        }

        model.addAttribute("students", studentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("classId", classId);
        // Add all classes for the filter dropdown
        model.addAttribute("classes", classRepository.findAll(org.springframework.data.domain.Sort.by("name")));

        // Pagination Logic
        int totalPages = studentPage.getTotalPages();
        if (totalPages > 0) {
            java.util.List<Integer> pageNumbers = new java.util.ArrayList<>();
            int pageNumber = studentPage.getNumber() + 1; // 1-based current page

            pageNumbers.add(1);

            for (int i = 2; i < totalPages; i++) {
                if (i == 2 || i == totalPages - 1 || Math.abs(i - pageNumber) <= 1) {
                    pageNumbers.add(i);
                } else if (pageNumbers.get(pageNumbers.size() - 1) != -1) {
                    pageNumbers.add(-1);
                }
            }

            if (totalPages > 1) {
                pageNumbers.add(totalPages);
            }
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "students";
    }

    /**
     * Shows the form for creating a new student.
     *
     * @param model The UI model.
     * @return The "student_form" view name.
     */
    /**
     * Shows the form for creating a new student.
     *
     * @param model   The UI model.
     * @param classId Optional query parameter to pre-select a class.
     * @return The "student_form" view name.
     */
    // Helper to group classes for dropdown
    private java.util.Map<String, java.util.Map<String, java.util.List<com.num.management.model.ClassEntity>>> getGroupedClasses() {
        return classRepository.findAll(org.springframework.data.domain.Sort.by("name")).stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        c -> c.getFaculty() == null || c.getFaculty().isEmpty() ? "Other" : c.getFaculty(),
                        java.util.TreeMap::new,
                        java.util.stream.Collectors.groupingBy(
                                c -> c.getDepartment() == null || c.getDepartment().isEmpty() ? "Other"
                                        : c.getDepartment(),
                                java.util.TreeMap::new,
                                java.util.stream.Collectors.toList())));
    }

    @GetMapping("/students/new")
    public String showNewStudentForm(Model model,
            @org.springframework.web.bind.annotation.RequestParam(value = "classId", required = false) Long classId) {
        log.info("Request to show new student form");
        com.num.management.model.Student student = new com.num.management.model.Student();

        if (classId != null) {
            classRepository.findById(classId).ifPresent(student::setClassEntity);
        }

        model.addAttribute("student", student);
        // Pass grouped classes instead of flat list for better UX
        model.addAttribute("groupedClasses", getGroupedClasses());
        return "student_form";
    }

    @org.springframework.web.bind.annotation.PostMapping("/students/save")
    public String saveStudent(
            @jakarta.validation.Valid @org.springframework.web.bind.annotation.ModelAttribute("student") com.num.management.model.Student student,
            org.springframework.validation.BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            log.warn("Validation errors while saving student: {}", result.getAllErrors());
            model.addAttribute("groupedClasses", getGroupedClasses());
            return "student_form";
        }
        studentRepository.save(student);
        log.info("Student saved successfully with ID: {}", student.getId());
        return "redirect:/students";
    }

    @GetMapping("/students/edit/{id}")
    public String showEditForm(@org.springframework.web.bind.annotation.PathVariable("id") Long id, Model model) {
        com.num.management.model.Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));
        model.addAttribute("student", student);
        model.addAttribute("groupedClasses", getGroupedClasses());
        return "student_form";
    }

    @org.springframework.web.bind.annotation.PostMapping("/students/update/{id}")
    public String updateStudent(@org.springframework.web.bind.annotation.PathVariable("id") Long id,
            @jakarta.validation.Valid @org.springframework.web.bind.annotation.ModelAttribute("student") com.num.management.model.Student student,
            org.springframework.validation.BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            student.setId(id);
            model.addAttribute("groupedClasses", getGroupedClasses());
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

        long total = attendanceList.size();
        long present = attendanceList.stream().filter(a -> "Present".equalsIgnoreCase(a.getStatus())).count();
        double rate = total > 0 ? (double) present / total * 100 : 0.0;

        model.addAttribute("student", student);
        model.addAttribute("attendanceList", attendanceList);
        model.addAttribute("attendanceRate", String.format("%.1f", rate));

        return "student_details";
    }
}
