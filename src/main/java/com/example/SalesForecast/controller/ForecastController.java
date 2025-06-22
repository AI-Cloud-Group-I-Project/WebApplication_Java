package com.example.SalesForecast.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ForecastController {
    @GetMapping("/forecast")
    public String showForecastPage(Model model, HttpSession session) {
        // ログイン情報
        String loginUserName = (String) session.getAttribute("username");
        String loginUserEmail = (String) session.getAttribute("email");
        String loginUserRole = (String) session.getAttribute("login_user_role");
        String loginUserStatus = (String) session.getAttribute("login_user_status");

        if (loginUserName == null) {
            return "access-denied";
        }
        if (!loginUserRole.equals("admin") && !loginUserRole.equals("user") && !loginUserRole.equals("viewer")) {
            return "authority-denied";
        }

        model.addAttribute("current_username", loginUserName);
        model.addAttribute("current_email", loginUserEmail);
        model.addAttribute("login_user_role", loginUserRole);
        model.addAttribute("login_user_status", loginUserStatus);

        return "forecast";
    }
}
