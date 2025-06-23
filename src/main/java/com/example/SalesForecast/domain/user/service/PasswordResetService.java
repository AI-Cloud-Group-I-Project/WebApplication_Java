package com.example.SalesForecast.domain.user.service;

import com.example.SalesForecast.domain.user.entity.LoginCredential;
import com.example.SalesForecast.domain.user.repository.LoginCredentialRepository;
import com.example.SalesForecast.domain.user.repository.UserRepository;
import com.example.SalesForecast.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
public class PasswordResetService {
    private final UserRepository userRepository;
    private final LoginCredentialRepository loginCredentialRepository;

    public PasswordResetService(UserRepository userRepository,
                                 LoginCredentialRepository loginCredentialRepository) {
        this.userRepository = userRepository;
        this.loginCredentialRepository = loginCredentialRepository;
    }

    public boolean resetPassword(String email, String newPassword) {
        var user = userRepository.findByEmail(email);
        if (user == null) return false;

        var credentialOpt = loginCredentialRepository.findById(user.getId());
        if (credentialOpt.isEmpty()) return false;

        var credential = credentialOpt.get();
        credential.setPasswordHash(PasswordUtil.hash(newPassword));
        credential.setEditedDate(LocalDate.now());
        loginCredentialRepository.save(credential);

        return true;
    }
}
