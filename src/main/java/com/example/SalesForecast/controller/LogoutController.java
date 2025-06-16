package com.example.SalesForecast.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class LogoutController {
    @PostMapping("/logout")
    public String backToLogin(HttpSession session) {
        if (session != null) {
            session.invalidate(); 
        }
        return "redirect:/";
    }
}
