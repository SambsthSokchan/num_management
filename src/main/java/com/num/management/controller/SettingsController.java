package com.num.management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    // View the settings page
    @GetMapping
    public String viewSettings() {
        return "settings"; // Return the settings view template
    }
}
