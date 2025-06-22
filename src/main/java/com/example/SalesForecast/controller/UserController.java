package com.example.SalesForecast.controller;

import com.example.SalesForecast.domain.user.repository.RoleRepository;
import com.example.SalesForecast.domain.user.service.UserService;
import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import com.example.SalesForecast.domain.user.entity.User;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public UserController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            Model model,
            HttpSession session) {

        Page<User> userPage = userService.getUsersByPage(PageRequest.of(page, 10));

        // ログイン情報
        String loginUserName = (String) session.getAttribute("username");
        String loginUserEmail = (String) session.getAttribute("email");
        String loginUserRole = (String) session.getAttribute("login_user_role");
        String loginUserStatus = (String) session.getAttribute("login_user_status");

        if (loginUserName == null) {
            return "access-denied";
        }
        if (!loginUserRole.equals("admin")) {
            return "authority-denied";
        }

        model.addAttribute("current_username", loginUserName);
        model.addAttribute("current_email", loginUserEmail);
        model.addAttribute("login_user_role", loginUserRole);
        model.addAttribute("login_user_status", loginUserStatus);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());

        model.addAttribute("roles", roleRepository.findAll());

        return "user-management";
    }

    @PostMapping("/update")
    public String handleSubmit(
            @RequestParam(required = false) Integer id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam("role.id") Integer roleId,
            @RequestParam String status,
            @RequestParam String action) {

        if (id != null) {
            userService.updateUserInfo(id, name, email, roleId, status);
        } else {
            userService.createUserWithCredential(name, email, roleId, status);
        }

        return "redirect:/users";
    }

    @PostMapping("/password-force-reset")
    public String passwordForceReset(@RequestParam(required = false) Integer id,
            RedirectAttributes ra) {
        userService.resetPassword(id);
        ra.addFlashAttribute("message", "パスワードをリセットしました。");
        return "redirect:/users";
    }
}
