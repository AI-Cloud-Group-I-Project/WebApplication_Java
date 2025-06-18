package com.example.SalesForecast.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ForecastController {
    @GetMapping("/forecast")
    public String showForecastPage(Model model, HttpSession session) {
        model.addAttribute("current_username", session.getAttribute("username"));
        model.addAttribute("current_email", session.getAttribute("email"));

        return "admin-forecast";
    }
}
