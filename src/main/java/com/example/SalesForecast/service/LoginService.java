package com.example.SalesForecast.service;

import com.example.SalesForecast.model.Users;
import com.example.SalesForecast.repository.LoginSpringBootRepository;
import com.example.SalesForecast.util.PasswordUtil;

import org.springframework.stereotype.Service;


@Service
public class LoginService {
    private final LoginSpringBootRepository repository;

    public LoginService(LoginSpringBootRepository repository) {
        this.repository = repository;
    }

    public boolean login(String username, String password) {
        Users user = repository.findByUserName(username);
        if (user == null)
            return false;

        String hashed = PasswordUtil.hash(password); //入力したパスワードをハッシュ
        return user.getPasswordHash().equals(hashed); //
    }
}