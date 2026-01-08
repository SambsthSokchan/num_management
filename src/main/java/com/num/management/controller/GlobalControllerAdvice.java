package com.num.management.controller;

import com.num.management.model.User;
import com.num.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private jakarta.servlet.http.HttpServletRequest request;

    // Make the current URI available to all views for highlighting active menu
    // items
    @ModelAttribute("currentUri")
    public String getCurrentUri() {
        return request.getRequestURI();
    }

    // specific logged-in user details to all views
    @ModelAttribute("loggedInUser")
    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check if user is authenticated and not anonymous
        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            String email = authentication.getName();
            // Retrieve full user details from database using email
            return userRepository.findByEmail(email).orElse(null);
        }
        return null; // Return null if not logged in
    }

    // Add 'role' model attribute for Sidebar logic
    @ModelAttribute("role")
    public String getUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            String auth = authentication.getAuthorities().stream()
                    .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                    .findFirst().orElse("");
            if (auth.startsWith("ROLE_")) {
                return auth.substring(5); // Remove "ROLE_" prefix (e.g. ADMIN)
            }
            return auth;
        }
        return null;
    }
}
