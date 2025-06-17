package com.example.SalesForecast.controller;

import com.example.SalesForecast.domain.user.entity.Role;
import com.example.SalesForecast.domain.user.entity.User;
import com.example.SalesForecast.domain.user.repository.RoleRepository;
import com.example.SalesForecast.domain.user.service.UserService;
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
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", roleRepository.findAll());
        return "user-management"; // user-management.htmlを表示
    }

    // 新規追加 (Addボタンで呼ぶ想定)
    @PostMapping("/add")
    public String addUser() {
        // 既定のロール名"User"を使用して初期値を作成
        Role defaultRole = roleRepository.findByName("User");

        User newUser = new User();
        newUser.setName("");
        newUser.setEmail("");
        newUser.setRole(defaultRole);
        newUser.setStatus("active"); // statusは文字列管理
        userService.createUser(newUser);
        return "redirect:/users";
    }

    // 更新 (Submitボタンで呼ぶ想定)
    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user) {
        userService.updateUser(user);
        return "redirect:/users";
    }
}
