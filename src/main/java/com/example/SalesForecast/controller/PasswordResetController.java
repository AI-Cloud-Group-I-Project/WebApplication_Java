package com.example.SalesForecast.controller;

import com.example.SalesForecast.domain.user.service.PasswordResetService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    // GET: パスワード再設定画面の表示
    @GetMapping("/reset-password")
    public String showResetPage(HttpSession session) {
        Boolean needsReset = (Boolean) session.getAttribute("forcePasswordReset");
        if (needsReset != null && needsReset) {
            return "password-reset-page";
        } else {
            return "redirect:/login";
        }
    }

    // POST: パスワード再設定の処理
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model,
                                 HttpSession session) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "パスワードが一致しません");
            return "password-reset-page";
        }

        // セッションから email を取得
        String email = (String) session.getAttribute("email");
        if (email == null) {
            model.addAttribute("error", "セッション情報が見つかりません。再度ログインしてください。");
            return "password-reset-page";
        }

        // パスワード更新処理
        boolean success = passwordResetService.resetPassword(email, newPassword);
        if (success) {
            session.removeAttribute("forcePasswordReset");
            return "redirect:/login";
        } else {
            model.addAttribute("error", "パスワード再設定に失敗しました");
            return "password-reset-page";
        }
    }
}


