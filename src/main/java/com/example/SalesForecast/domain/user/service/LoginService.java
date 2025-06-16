package com.example.SalesForecast.domain.user.service;

import com.example.SalesForecast.domain.user.entity.User;
import com.example.SalesForecast.domain.user.entity.LoginCredential;
import com.example.SalesForecast.domain.user.repository.UserRepository;
import com.example.SalesForecast.domain.user.repository.LoginCredentialRepository;
import com.example.SalesForecast.util.PasswordUtil;

import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final UserRepository userRepository;
    private final LoginCredentialRepository loginCredentialRepository;

    public LoginService(UserRepository userRepository, LoginCredentialRepository loginCredentialRepository) {
        this.userRepository = userRepository;
        this.loginCredentialRepository = loginCredentialRepository;
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null)
            return null;

        // findByIdの戻り値は Optional型 -> orElseで実態 or nullにできる
        LoginCredential credential = loginCredentialRepository.findById(user.getId()).orElse(null);
        String hashed = PasswordUtil.hash(password); // 入力したパスワードをハッシュ化
        if (credential.getPasswordHash().equals(hashed)) {
            return user;
        } else
            return null;
    }
}