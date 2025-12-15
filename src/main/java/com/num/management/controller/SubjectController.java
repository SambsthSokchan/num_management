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

    @GetMapping("/subjects")
    public String listSubjects(Model model) {
        model.addAttribute("subjects", subjectRepository.findAll());
        return "subjects";
    }

    @GetMapping("/subjects/new")
    public String showNewSubjectForm(Model model) {
        com.num.management.model.Subject subject = new com.num.management.model.Subject();
        model.addAttribute("subject", subject);
        return "subject_form";
    }

    @org.springframework.web.bind.annotation.PostMapping("/subjects/save")
    public String saveSubject(
            @org.springframework.web.bind.annotation.ModelAttribute("subject") com.num.management.model.Subject subject) {
        subjectRepository.save(subject);
        return "redirect:/subjects";
    }

    @GetMapping("/subjects/edit/{id}")
    public String showEditForm(@org.springframework.web.bind.annotation.PathVariable("id") Long id, Model model) {
        com.num.management.model.Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject Id:" + id));
        model.addAttribute("subject", subject);
        return "subject_form";
    }

    @org.springframework.web.bind.annotation.PostMapping("/subjects/update/{id}")
    public String updateSubject(@org.springframework.web.bind.annotation.PathVariable("id") Long id,
            @org.springframework.web.bind.annotation.ModelAttribute("subject") com.num.management.model.Subject subject,
            Model model) {
        subject.setId(id);
        subjectRepository.save(subject);
        return "redirect:/subjects";
    }

    @GetMapping("/subjects/delete/{id}")
    public String deleteSubject(@org.springframework.web.bind.annotation.PathVariable("id") Long id) {
        com.num.management.model.Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject Id:" + id));
        subjectRepository.delete(subject);
        return "redirect:/subjects";
    }

    @Autowired
    private com.num.management.repository.TeacherRepository teacherRepository;

    @GetMapping("/subjects/{id}")
    public String viewSubject(@org.springframework.web.bind.annotation.PathVariable("id") Long id, Model model) {
        com.num.management.model.Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject Id:" + id));
        model.addAttribute("subject", subject);
        model.addAttribute("teachers", teacherRepository.findBySubjectEntityId(id));
        return "subject_details";
    }
}
