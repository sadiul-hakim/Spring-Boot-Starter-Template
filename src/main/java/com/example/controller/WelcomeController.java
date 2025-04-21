package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WelcomeController {

    private int count = 0;

    @GetMapping("/")
    String welcome(Model model) {

        model.addAttribute("count", count);
        return "index";
    }

    @GetMapping("/login_page")
    String loginPage(Model model) {

        model.addAttribute("count", count);
        return "login_page";
    }

    @PostMapping("/increment")
    String increment(Model model) {
        count++;
        model.addAttribute("count", count);
        return "index :: count-display";
    }
}
