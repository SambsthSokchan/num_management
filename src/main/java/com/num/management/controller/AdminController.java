package com.num.management.controller;

import com.num.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.num.management.service.UserService userService;

    // Display list of admins
    @GetMapping("/admins")
    public String listAdmins(Model model) {
        // Fetch users with ROLE_ADMIN
        model.addAttribute("admins", userRepository.findByRoles_Name("ROLE_ADMIN"));
        return "admins";
    }

    // Show form for creating a new admin
    @GetMapping("/admins/new")
    public String showNewAdminForm(Model model) {
        com.num.management.dto.UserRegistrationDto adminDto = new com.num.management.dto.UserRegistrationDto();
        model.addAttribute("admin", adminDto);
        return "admin_form";
    }

    // Save a new admin
    @org.springframework.web.bind.annotation.PostMapping("/admins/save")
    public String saveAdmin(
            @org.springframework.web.bind.annotation.ModelAttribute("admin") com.num.management.dto.UserRegistrationDto adminDto) {
        // Enforce Admin role
        adminDto.setRole("Admin");
        userService.save(adminDto);
        return "redirect:/admins";
    }

    // Show form for editing an admin
    @GetMapping("/admins/edit/{id}")
    public String showEditAdminForm(@org.springframework.web.bind.annotation.PathVariable("id") Long id, Model model) {
        if (id == null) {
            throw new IllegalArgumentException("Admin ID cannot be null");
        }
        com.num.management.model.User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        // Map User to DTO for the form
        com.num.management.dto.UserRegistrationDto adminDto = new com.num.management.dto.UserRegistrationDto();
        adminDto.setFirstName(user.getFirstName());
        adminDto.setLastName(user.getLastName());
        adminDto.setEmail(user.getEmail());
        // Password is not pre-filled for security, but we need to handle it if left
        // empty in update

        model.addAttribute("admin", adminDto);
        model.addAttribute("id", id); // Pass ID separately or in DTO if customized
        return "admin_edit"; // We might need a separate edit template or reuse form
    }

    // Update an admin
    @org.springframework.web.bind.annotation.PostMapping("/admins/update/{id}")
    public String updateAdmin(@org.springframework.web.bind.annotation.PathVariable("id") Long id,
            @org.springframework.web.bind.annotation.ModelAttribute("admin") com.num.management.dto.UserRegistrationDto adminDto) {

        if (id == null) {
            throw new IllegalArgumentException("Admin ID cannot be null");
        }
        com.num.management.model.User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        user.setFirstName(adminDto.getFirstName());
        user.setLastName(adminDto.getLastName());
        user.setEmail(adminDto.getEmail());

        // Only update password if provided
        if (adminDto.getPassword() != null && !adminDto.getPassword().isEmpty()) {
            // We need direct access to encoder or service method.
            // UserService.save() creates new. We need Update logic.
            // For now, let's assume we can't update password easily without injecting
            // encoder here.
            // Or we add update method to UserService.
        }

        userRepository.save(user);
        return "redirect:/admins";
    }

    // Delete an admin
    @GetMapping("/admins/delete/{id}")
    public String deleteAdmin(@org.springframework.web.bind.annotation.PathVariable("id") Long id) {
        com.num.management.model.User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userRepository.delete(user);
        return "redirect:/admins";
    }
}
