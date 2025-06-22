package com.example.SalesForecast.controller;

import com.example.SalesForecast.domain.user.service.LoginService;
import com.example.SalesForecast.domain.user.entity.User;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/")
    public String index() {
        return "login";
    }

    @PostMapping("/login")
    public String post(@RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session) {
        User user = loginService.login(email, password);
        if (user != null) {
            if ("retired".equals(user.getStatus())) {
                model.addAttribute("error", "退職済みのユーザーはログインできません。");
                return "login";
            }
            session.setAttribute("username", user.getName());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("login_user_role", user.getRole().getName());
            session.setAttribute("login_user_status", user.getStatus());
            return "redirect:/sales-weather";
        } else {
            model.addAttribute("error", "Emailまたがパスワードが正しくありません。");
            return "login";
        }
    }
}