package com.example.Login_Spring_Boot.controller;

import com.example.Login_Spring_Boot.service.LoginService;

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
        return "index";
    }

    @PostMapping("/")
    public String post(@RequestParam String username,
            @RequestParam String password,
            Model model,
            HttpSession session) {
        if (loginService.login(username, password)) {
            session.setAttribute("username", username);
            return "redirect:/welcome";
        } else {
            model.addAttribute("error", "ログイン失敗");
            return "index";
        }
    }
}