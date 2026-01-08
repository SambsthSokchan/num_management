package com.num.management.controller;

import com.num.management.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TeacherController {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private com.num.management.repository.SubjectRepository subjectRepository;

    // Display list of teachers, optionally filtering by keyword
    @GetMapping("/teachers")
    public String listTeachers(Model model,
            @org.springframework.web.bind.annotation.RequestParam(value = "keyword", required = false) String keyword,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size) {

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by("name"));
        org.springframework.data.domain.Page<com.num.management.model.Teacher> teacherPage;

        if (keyword != null && !keyword.isEmpty()) {
            // Search teachers by name if keyword provided
            teacherPage = teacherRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            // Otherwise, get all teachers
            teacherPage = teacherRepository.findAll(pageable);
        }

        model.addAttribute("teachers", teacherPage);
        model.addAttribute("keyword", keyword); // Maintain search keyword in UI

        // Pagination Logic
        int totalPages = teacherPage.getTotalPages();
        if (totalPages > 0) {
            java.util.List<Integer> pageNumbers = new java.util.ArrayList<>();
            int pageNumber = teacherPage.getNumber() + 1; // 1-based current page

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

        return "teachers";
    }

    // Show form for creating a new teacher
    @GetMapping("/teachers/new")
    public String showNewTeacherForm(Model model) {
        // Create empty Teacher object
        com.num.management.model.Teacher teacher = new com.num.management.model.Teacher();
        model.addAttribute("teacher", teacher);
        // Load subjects for dropdown selection
        model.addAttribute("subjects", subjectRepository.findAll());
        return "teacher_form";
    }

    // Save a new teacher to the database
    @org.springframework.web.bind.annotation.PostMapping("/teachers/save")
    public String saveTeacher(
            @jakarta.validation.Valid @org.springframework.web.bind.annotation.ModelAttribute("teacher") com.num.management.model.Teacher teacher,
            org.springframework.validation.BindingResult result,
            Model model) {
        // Validation check
        if (result.hasErrors()) {
            // Reload subjects if validation fails
            model.addAttribute("subjects", subjectRepository.findAll());
            return "teacher_form";
        }
        teacherRepository.save(teacher);
        return "redirect:/teachers";
    }

    // Display teacher details
    @GetMapping("/teachers/{id}")
    public String getTeacherDetail(@org.springframework.web.bind.annotation.PathVariable("id") Long id, Model model) {
        com.num.management.model.Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid teacher Id:" + id));
        model.addAttribute("teacher", teacher);
        return "teacher_detail";
    }

    // Display form for editing an existing teacher
    @GetMapping("/teachers/edit/{id}")
    public String showEditForm(@org.springframework.web.bind.annotation.PathVariable("id") Long id, Model model) {
        // Find teacher by ID or throw error
        com.num.management.model.Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid teacher Id:" + id));
        model.addAttribute("teacher", teacher);
        // Load subjects for dropdown
        model.addAttribute("subjects", subjectRepository.findAll());
        return "teacher_form";
    }

    // Update an existing teacher record
    @org.springframework.web.bind.annotation.PostMapping("/teachers/update/{id}")
    public String updateTeacher(@org.springframework.web.bind.annotation.PathVariable("id") Long id,
            @jakarta.validation.Valid @org.springframework.web.bind.annotation.ModelAttribute("teacher") com.num.management.model.Teacher teacher,
            org.springframework.validation.BindingResult result,
            Model model) {
        // Validation check
        if (result.hasErrors()) {
            teacher.setId(id); // Retain ID
            model.addAttribute("subjects", subjectRepository.findAll());
            return "teacher_form";
        }
        // Ensure the ID is set so it updates instead of inserting
        teacher.setId(id);
        teacherRepository.save(teacher);
        return "redirect:/teachers";
    }

    // Delete a teacher by ID
    @GetMapping("/teachers/delete/{id}")
    public String deleteTeacher(@org.springframework.web.bind.annotation.PathVariable("id") Long id) {
        com.num.management.model.Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid teacher Id:" + id));
        teacherRepository.delete(teacher);
        return "redirect:/teachers";
    }
}
