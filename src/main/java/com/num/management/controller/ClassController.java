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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.TreeMap;
import java.util.HashMap;

@Controller
public class ClassController {

        @Autowired
        private ClassRepository classRepository;

        @Autowired
        private com.num.management.repository.StudentRepository studentRepository;

        // Display list of all classes available in the system
        @GetMapping("/classes")
        public String listClasses(Model model) {
                // Retrieve all classes
                List<ClassEntity> allClasses = classRepository.findAll(org.springframework.data.domain.Sort.by("name"));

                // Group by Faculty -> Department -> List<ClassEntity>
                // Using TreeMap to sort keys (Faculty names and Department names)
                // alphabetically
                Map<String, Map<String, List<ClassEntity>>> groupedClasses = allClasses.stream()
                                .collect(Collectors.groupingBy(
                                                c -> c.getFaculty() == null || c.getFaculty().isEmpty()
                                                                ? "Unassigned Faculty"
                                                                : c.getFaculty(),
                                                TreeMap::new,
                                                Collectors.groupingBy(
                                                                c -> c.getDepartment() == null || c.getDepartment()
                                                                                .isEmpty() ? "Unassigned Department"
                                                                                                : c.getDepartment(),
                                                                TreeMap::new,
                                                                Collectors.toList())));

                model.addAttribute("groupedClasses", groupedClasses);
                model.addAttribute("classes", allClasses); // Keep flat list if needed elsewhere or for debug

                // Hardcoded list of ALL faculties for the filter (even if empty)
                java.util.List<String> allFaculties = java.util.Arrays.asList(
                                "Faculty of Management",
                                "Faculty of Finance & Accounting",
                                "Faculty of Economics",
                                "Faculty of Information Technology",
                                "Faculty of Law",
                                "Faculty of Tourism & Hospitality",
                                "Faculty of Foreign Languages");
                model.addAttribute("faculties", allFaculties);

                // Add Faculty Descriptions
                Map<String, String> facultyDescriptions = new HashMap<>();
                facultyDescriptions.put("Faculty of Management",
                                "Focuses on general leadership, marketing, and global business.");
                facultyDescriptions.put("Faculty of Finance & Accounting",
                                "Highly regarded for professional banking and financial management.");
                facultyDescriptions.put("Faculty of Economics",
                                "Covers the core theories of micro and macro-level development.");
                facultyDescriptions.put("Faculty of Information Technology",
                                "Specializes in MIS and robotic engineering.");
                facultyDescriptions.put("Faculty of Law",
                                "Offers training in both national and international legal frameworks.");
                facultyDescriptions.put("Faculty of Tourism & Hospitality",
                                "Provides specialized management skills for the hospitality industry.");
                facultyDescriptions.put("Faculty of Foreign Languages",
                                "Focuses on linguistics and literature for global communication.");

                model.addAttribute("facultyDescriptions", facultyDescriptions);

                return "classes";
        }

        // Helper class for TreeMap supplier to avoid obscure lambda casting issues if
        // any,
        // but actually we can just use TreeMap::new directly in the collector if we
        // cast it or use specific method.
        // Let's simplify and use default HashMap grouping then sort in View or use a
        // simpler TreeMap approach.
        // Actually, java 8 collectors with supplier:
        // Collectors.groupingBy(classifier, mapFactory, downstream)

        /*
         * Re-writing the method body below cleanly without the inner comment mess.
         */

        // Show the form for creating a new class
        @GetMapping("/classes/new")
        public String showNewClassForm(Model model) {
                // Create an empty ClassEntity to bind form data
                ClassEntity classEntity = new ClassEntity();
                model.addAttribute("classEntity", classEntity);
                return "class_form";
        }

        // Save a new class to the database
        @PostMapping("/classes/save")
        public String saveClass(@ModelAttribute("classEntity") ClassEntity classEntity) {
                // Persist the new class entity
                classRepository.save(classEntity);
                return "redirect:/classes"; // Redirect to class list after saving
        }

        // Display the form for editing an existing class
        @GetMapping("/classes/edit/{id}")
        public String showEditForm(@PathVariable("id") Long id, Model model) {
                // Retrieve class by ID, ensuring it exists
                ClassEntity classEntity = classRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid class Id:" + id));
                model.addAttribute("classEntity", classEntity);
                return "class_form";
        }

        // Update an existing class record
        @PostMapping("/classes/update/{id}")
        public String updateClass(@PathVariable("id") Long id,
                        @ModelAttribute("classEntity") ClassEntity classEntity,
                        Model model) {
                // Set ID to ensure update operation logic is triggered
                classEntity.setId(id);
                classRepository.save(classEntity);
                return "redirect:/classes";
        }

        // Delete a class by ID
        @GetMapping("/classes/delete/{id}")
        public String deleteClass(@PathVariable("id") Long id) {
                // Check existence before deletion to handle errors gracefully
                ClassEntity classEntity = classRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid class Id:" + id));
                classRepository.delete(classEntity);
                return "redirect:/classes";
        }

        @GetMapping("/classes/{id}")
        @SuppressWarnings("null")
        public String viewClass(@PathVariable("id") Long id, Model model,
                        @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
                        @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size) {
                // Retrieve class details
                ClassEntity classEntity = classRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid class Id:" + id));
                model.addAttribute("classEntity", classEntity);

                // Retrieve and add students associated with this class with pagination
                org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page,
                                size,
                                org.springframework.data.domain.Sort.by("name"));
                org.springframework.data.domain.Page<com.num.management.model.Student> studentPage = studentRepository
                                .findByClassEntityId(id, pageable);

                model.addAttribute("students", studentPage); // Pass the Page object, not just content
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", studentPage.getTotalPages());
                model.addAttribute("totalItems", studentPage.getTotalElements());

                // Pagination metadata for loop
                if (studentPage.getTotalPages() > 0) {
                        java.util.List<Integer> pageNumbers = java.util.stream.IntStream
                                        .rangeClosed(1, studentPage.getTotalPages())
                                        .boxed()
                                        .collect(Collectors.toList());
                        model.addAttribute("pageNumbers", pageNumbers);
                }

                return "class_details";
        }
}
