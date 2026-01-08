package com.num.management.controller;

import com.num.management.dto.UserRegistrationDto;
import com.num.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // Display the login page
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Display user registration page
    @GetMapping("/signup")
    public String showRegistrationForm(Model model) {
        // Create empty DTO for form binding
        UserRegistrationDto user = new UserRegistrationDto();
        model.addAttribute("user", user);
        return "signup";
    }

    // Handle user registration process
    @PostMapping("/signup")
    public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto) {
        // Save new user using the service layer
        userService.save(registrationDto);
        return "redirect:/signup?success"; // Redirect with success flag
    }
}
