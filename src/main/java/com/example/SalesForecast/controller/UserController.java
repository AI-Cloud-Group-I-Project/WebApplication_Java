package com.example.SalesForecast.controller;

import com.example.SalesForecast.domain.user.entity.Role;
import com.example.SalesForecast.domain.user.entity.User;
import com.example.SalesForecast.domain.user.repository.RoleRepository;
import com.example.SalesForecast.domain.user.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public UserController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    // 一覧表示
    @GetMapping
    public String listUsers(Model model, HttpSession session) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", roleRepository.findAll());

        model.addAttribute("current_username", session.getAttribute("username"));
        model.addAttribute("current_email", session.getAttribute("email"));
        return "admin-user-management";
    }

    // 更新 (Submitボタンで呼ぶ想定)
    @PostMapping("/update")
    public String handleSubmit(
            @RequestParam(required = false) Integer id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam("role.id") Integer roleId,
            @RequestParam String action) {

        if (id != null) {
            // 更新処理
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));
            user.setName(name);
            user.setEmail(email);
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new IllegalArgumentException("ロールが見つかりません"));
            user.setRole(role);
            userService.updateUser(user);
        } else {
            // 新規登録処理
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new IllegalArgumentException("ロールが見つかりません"));
            user.setRole(role);
            user.setStatus("active"); // 必要に応じて
            userService.createUser(user);
        }

        return "redirect:/users";
    }
}
