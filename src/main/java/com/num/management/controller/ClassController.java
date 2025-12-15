package com.num.management.controller;

import com.num.management.model.ClassEntity;
import com.num.management.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ClassController {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private com.num.management.repository.StudentRepository studentRepository;

    @GetMapping("/classes")
    public String listClasses(Model model) {
        model.addAttribute("classes", classRepository.findAll());
        return "classes";
    }

    @GetMapping("/classes/new")
    public String showNewClassForm(Model model) {
        ClassEntity classEntity = new ClassEntity();
        model.addAttribute("classEntity", classEntity);
        return "class_form";
    }

    @PostMapping("/classes/save")
    public String saveClass(@ModelAttribute("classEntity") ClassEntity classEntity) {
        classRepository.save(classEntity);
        return "redirect:/classes";
    }

    @GetMapping("/classes/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        ClassEntity classEntity = classRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid class Id:" + id));
        model.addAttribute("classEntity", classEntity);
        return "class_form";
    }

    @PostMapping("/classes/update/{id}")
    public String updateClass(@PathVariable("id") Long id,
            @ModelAttribute("classEntity") ClassEntity classEntity,
            Model model) {
        classEntity.setId(id);
        classRepository.save(classEntity);
        return "redirect:/classes";
    }

    @GetMapping("/classes/delete/{id}")
    public String deleteClass(@PathVariable("id") Long id) {
        ClassEntity classEntity = classRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid class Id:" + id));
        classRepository.delete(classEntity);
        return "redirect:/classes";
    }

    @GetMapping("/classes/{id}")
    public String viewClass(@PathVariable("id") Long id, Model model) {
        ClassEntity classEntity = classRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid class Id:" + id));
        model.addAttribute("classEntity", classEntity);
        model.addAttribute("students", studentRepository.findByClassEntityId(id));
        return "class_details";
    }
}
