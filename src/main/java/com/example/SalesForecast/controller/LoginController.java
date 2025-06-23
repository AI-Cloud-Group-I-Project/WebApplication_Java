package com.example.SalesForecast.controller;

import com.example.SalesForecast.domain.user.service.LoginService;
import com.example.SalesForecast.domain.user.entity.User;
import com.example.SalesForecast.util.PasswordUtil;

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

    @GetMapping("/login")  
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String post(@RequestParam String email,
        @RequestParam String password,
        Model model,
        HttpSession session) {
    User user = loginService.login(email, password);
    if (user != null) {
        session.setAttribute("username", user.getName());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("login_user_role", user.getRole().getName());

        // 初期パスワードか判定
        String hashedInput = PasswordUtil.hash(password);
        String initialHashed = PasswordUtil.hash("password1234");
        if (hashedInput.equals(initialHashed)) {
            session.setAttribute("forcePasswordReset", true);
            return "redirect:/reset-password";
        }

        return "redirect:/sales-weather";
    } else {
        model.addAttribute("error", "ログイン失敗");
        return "login";
    }
}
}
