package com.num.management.controller;

import com.num.management.model.User;
import com.num.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    // View the user's profile
    @GetMapping
    public String viewProfile(Model model, java.security.Principal principal) {
        String email = principal.getName();
        // Fetch current user or default to new User to avoid null pointer issues
        User currentUser = userRepository.findByEmail(email).orElse(new User());

        model.addAttribute("user", currentUser);
        return "profile";
    }

    // Show form to edit profile details
    @GetMapping("/edit")
    public String editProfile(Model model, java.security.Principal principal) {
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email).orElse(new User());
        model.addAttribute("user", currentUser);
        return "profile_form";
    }

    // Process profile updates (name, password)
    @org.springframework.web.bind.annotation.PostMapping("/update")
    public String updateProfile(@org.springframework.web.bind.annotation.ModelAttribute User user,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String newPassword) {

        // Fetch existing user to ensure we are updating the correct record
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        if (existingUser != null) {
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());

            // Only update password if a new one is provided
            if (newPassword != null && !newPassword.isEmpty()) {
                existingUser.setPassword(newPassword);
            }

            userRepository.save(existingUser);
        }
        return "redirect:/profile";
    }

    // Handle profile photo upload
    @org.springframework.web.bind.annotation.PostMapping("/upload")
    public String uploadPhoto(
            @org.springframework.web.bind.annotation.RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                // Generate a unique filename to prevent collisions
                String fileName = "profile_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
                fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_"); // Sanitize filename for safety

                // 1. Save to Source Directory (Persistence across rebuilds)
                String srcDir = "src/main/resources/static/images/";
                java.nio.file.Path srcPath = java.nio.file.Paths.get(srcDir);
                if (!java.nio.file.Files.exists(srcPath)) {
                    java.nio.file.Files.createDirectories(srcPath);
                }
                try (java.io.InputStream inputStream = file.getInputStream()) {
                    java.nio.file.Path targetPath = srcPath.resolve(fileName);
                    java.nio.file.Files.copy(inputStream, targetPath,
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                    // Optional: Overwrite admin-profile.jpg in Source if this is the admin (logic
                    // implies specific use case)
                    java.nio.file.Files.copy(targetPath, srcPath.resolve("admin-profile.jpg"),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }

                // 2. Save to Runtime Target Directory (Immediate View without restart)
                String targetDir = "target/classes/static/images/";
                java.nio.file.Path runtimePath = java.nio.file.Paths.get(targetDir);
                if (!java.nio.file.Files.exists(runtimePath)) {
                    java.nio.file.Files.createDirectories(runtimePath);
                }

                if (java.nio.file.Files.exists(runtimePath)) {
                    try (java.io.InputStream inputStream = file.getInputStream()) {
                        java.nio.file.Path targetRuntimePath = runtimePath.resolve(fileName);
                        java.nio.file.Files.copy(inputStream, targetRuntimePath,
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                        // Overwrite admin-profile.jpg in Runtime
                        java.nio.file.Files.copy(targetRuntimePath, runtimePath.resolve("admin-profile.jpg"),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                }

                // Update User entity with the new image path
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User currentUser = userRepository.findByEmail(auth.getName()).orElse(null);

                if (currentUser != null) {
                    currentUser.setProfilePicture("/images/" + fileName);
                    userRepository.save(currentUser);
                }

            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/profile";
    }

    // Remove the user's profile photo
    @org.springframework.web.bind.annotation.PostMapping("/delete-photo")
    public String deletePhoto() {
        System.out.println("DEBUG: deletePhoto called");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        System.out.println("DEBUG: User email: " + email);
        User currentUser = userRepository.findByEmail(email).orElse(null);

        if (currentUser != null) {
            System.out.println("DEBUG: User found. Current Profile Picture: " + currentUser.getProfilePicture());
            // Clear profile picture if one is set
            if (currentUser.getProfilePicture() != null && !currentUser.getProfilePicture().isEmpty()) {
                currentUser.setProfilePicture(null);
                userRepository.save(currentUser);
                System.out.println("DEBUG: Profile picture set to null and saved.");
            } else {
                System.out.println("DEBUG: Profile picture was already null or empty.");
            }
        } else {
            System.out.println("DEBUG: User not found!");
        }
        return "redirect:/profile";
    }
}
