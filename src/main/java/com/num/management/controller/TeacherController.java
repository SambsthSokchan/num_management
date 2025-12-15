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

    @GetMapping("/teachers")
    public String listTeachers(Model model,
            @org.springframework.web.bind.annotation.RequestParam(value = "keyword", required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("teachers", teacherRepository.findByNameContainingIgnoreCase(keyword));
        } else {
            model.addAttribute("teachers", teacherRepository.findAll());
        }
        model.addAttribute("keyword", keyword); // Keep the keyword in the search box
        return "teachers";
    }

    @GetMapping("/teachers/new")
    public String showNewTeacherForm(Model model) {
        com.num.management.model.Teacher teacher = new com.num.management.model.Teacher();
        model.addAttribute("teacher", teacher);
        model.addAttribute("subjects", subjectRepository.findAll());
        return "teacher_form";
    }

    @org.springframework.web.bind.annotation.PostMapping("/teachers/save")
    public String saveTeacher(
            @jakarta.validation.Valid @org.springframework.web.bind.annotation.ModelAttribute("teacher") com.num.management.model.Teacher teacher,
            org.springframework.validation.BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("subjects", subjectRepository.findAll());
            return "teacher_form";
        }
        teacherRepository.save(teacher);
        return "redirect:/teachers";
    }

    @GetMapping("/teachers/edit/{id}")
    public String showEditForm(@org.springframework.web.bind.annotation.PathVariable("id") Long id, Model model) {
        com.num.management.model.Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid teacher Id:" + id));
        model.addAttribute("teacher", teacher);
        model.addAttribute("subjects", subjectRepository.findAll()); // Fetch all subjects
        return "teacher_form";
    }

    @org.springframework.web.bind.annotation.PostMapping("/teachers/update/{id}")
    public String updateTeacher(@org.springframework.web.bind.annotation.PathVariable("id") Long id,
            @jakarta.validation.Valid @org.springframework.web.bind.annotation.ModelAttribute("teacher") com.num.management.model.Teacher teacher,
            org.springframework.validation.BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            teacher.setId(id);
            model.addAttribute("subjects", subjectRepository.findAll());
            return "teacher_form";
        }
        // Ensure the ID is set so it updates instead of inserting
        teacher.setId(id);
        teacherRepository.save(teacher);
        return "redirect:/teachers";
    }

    @GetMapping("/teachers/delete/{id}")
    public String deleteTeacher(@org.springframework.web.bind.annotation.PathVariable("id") Long id) {
        com.num.management.model.Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid teacher Id:" + id));
        teacherRepository.delete(teacher);
        return "redirect:/teachers";
    }
}
