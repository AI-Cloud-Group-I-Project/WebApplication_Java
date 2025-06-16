package com.example.Login_Spring_Boot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class WelcomeController {
    @GetMapping("/welcome")
    public String welcome(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/"; // 未ログインで直接来たら戻す
        }

        model.addAttribute("username", username);
        return "welcome";
    }

}
