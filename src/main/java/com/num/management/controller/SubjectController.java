package com.num.management.controller;

import com.num.management.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SubjectController {

    @Autowired
    private SubjectRepository subjectRepository;

    // Display list of subjects
    // Display list of subjects, optionally filtered by faculty
    // Display list of subjects, optionally filtered by faculty
    @GetMapping("/subjects")
    public String listSubjects(Model model,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String faculty,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size) {

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<com.num.management.model.Subject> subjects;

        if (faculty != null && !faculty.isEmpty() && !faculty.equals("all")) {
            subjects = subjectRepository.findByFaculty(faculty, pageable);
        } else {
            subjects = subjectRepository.findAll(pageable);
        }

        model.addAttribute("subjects", subjects);
        model.addAttribute("selectedFaculty", faculty);

        // Smart Pagination Logic
        int totalPages = subjects.getTotalPages();
        if (totalPages > 0) {
            java.util.List<Integer> pageNumbers = new java.util.ArrayList<>();
            int currentPage = subjects.getNumber() + 1;

            if (totalPages <= 7) {
                for (int i = 1; i <= totalPages; i++)
                    pageNumbers.add(i);
            } else {
                if (currentPage <= 4) {
                    for (int i = 1; i <= 5; i++)
                        pageNumbers.add(i);
                    pageNumbers.add(-1);
                    pageNumbers.add(totalPages);
                } else if (currentPage >= totalPages - 3) {
                    pageNumbers.add(1);
                    pageNumbers.add(-1);
                    for (int i = totalPages - 4; i <= totalPages; i++)
                        pageNumbers.add(i);
                } else {
                    pageNumbers.add(1);
                    pageNumbers.add(-1);
                    for (int i = currentPage - 1; i <= currentPage + 1; i++)
                        pageNumbers.add(i);
                    pageNumbers.add(-1);
                    pageNumbers.add(totalPages);
                }
            }
            model.addAttribute("pageNumbers", pageNumbers);
        }

        // Hardcoded list of faculties matching DataSeeder
        java.util.List<String> faculties = java.util.Arrays.asList(
                "Faculty of Information Technology",
                "Faculty of Management",
                "Faculty of Finance & Accounting",
                "Faculty of Economics",
                "Faculty of Law",
                "Faculty of Tourism & Hospitality",
                "Faculty of Foreign Languages");
        model.addAttribute("faculties", faculties);

        return "subjects";
    }

    // Show form for creating a new subject
    @GetMapping("/subjects/new")
    public String showNewSubjectForm(Model model) {
        com.num.management.model.Subject subject = new com.num.management.model.Subject();
        model.addAttribute("subject", subject);
        return "subject_form";
    }

    // Save a new subject to the database
    @org.springframework.web.bind.annotation.PostMapping("/subjects/save")
    @SuppressWarnings("null")
    public String saveSubject(
            @org.springframework.web.bind.annotation.ModelAttribute("subject") com.num.management.model.Subject subject) {
        subjectRepository.save(subject);
        return "redirect:/subjects";
    }

    // Display form for editing an existing subject
    @GetMapping("/subjects/edit/{id}")
    @SuppressWarnings("null")
    public String showEditForm(@org.springframework.web.bind.annotation.PathVariable("id") Long id, Model model) {
        com.num.management.model.Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject Id:" + id));
        model.addAttribute("subject", subject);
        return "subject_form";
    }

    // Update an existing subject
    @org.springframework.web.bind.annotation.PostMapping("/subjects/update/{id}")
    @SuppressWarnings("null")
    public String updateSubject(@org.springframework.web.bind.annotation.PathVariable("id") Long id,
            @org.springframework.web.bind.annotation.ModelAttribute("subject") com.num.management.model.Subject subject,
            Model model) {
        subject.setId(id); // Ensure correct ID for update
        subjectRepository.save(subject);
        return "redirect:/subjects";
    }

    // Delete a subject
    @GetMapping("/subjects/delete/{id}")
    public String deleteSubject(@org.springframework.web.bind.annotation.PathVariable("id") Long id) {
        com.num.management.model.Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject Id:" + id));

        // Unassign all teachers from this subject before deleting
        java.util.List<com.num.management.model.Teacher> teachers = teacherRepository.findBySubjectsId(id);
        for (com.num.management.model.Teacher teacher : teachers) {
            teacher.getSubjects().remove(subject);
            teacherRepository.save(teacher);
        }

        subjectRepository.delete(subject);
        return "redirect:/subjects";
    }

    @Autowired
    private com.num.management.repository.TeacherRepository teacherRepository;

    // View subject details including teachers assigned to it
    @GetMapping("/subjects/{id}")
    @SuppressWarnings("null")
    public String viewSubject(@org.springframework.web.bind.annotation.PathVariable("id") Long id, Model model) {
        com.num.management.model.Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject Id:" + id));
        model.addAttribute("subject", subject);
        // Load teachers associated with this subject
        model.addAttribute("teachers", teacherRepository.findBySubjectsId(id));
        return "subject_details";
    }
}
