package com.example.SalesForecast.domain.user.service;

import com.example.SalesForecast.domain.user.entity.LoginCredential;
import com.example.SalesForecast.domain.user.entity.Role;
import com.example.SalesForecast.domain.user.entity.User;
import com.example.SalesForecast.domain.user.repository.LoginCredentialRepository;
import com.example.SalesForecast.domain.user.repository.RoleRepository;
import com.example.SalesForecast.domain.user.repository.UserRepository;
import com.example.SalesForecast.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserService {
    @Value("${init.password}")
    private String INITAL_PASSWORD;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LoginCredentialRepository loginCredentialRepository;

    public UserService(UserRepository userRepository,
            RoleRepository roleRepository,
            LoginCredentialRepository loginCredentialRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.loginCredentialRepository = loginCredentialRepository;
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    @Transactional
    public void createUserWithCredential(String name, String email, Integer roleId, String status) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("ロールが見つかりません"));

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRole(role);
        user.setStatus(status);

        User savedUser = userRepository.save(user); // IDが確定

        LoginCredential credential = new LoginCredential();
        credential.setUser(savedUser);
        credential.setPasswordHash(PasswordUtil.hash(INITAL_PASSWORD));
        credential.setCreatedDate(LocalDate.now());
        credential.setEditedDate(LocalDate.now());

        loginCredentialRepository.save(credential);
    }

    @Transactional
    public void updateUserInfo(Integer id, String name, String email, Integer roleId, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("ロールが見つかりません"));

        user.setName(name);
        user.setEmail(email);
        user.setRole(role);
        user.setStatus(status);

        userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    public Page<User> getUsersByPage(Pageable pageable) {
        return userRepository.findAllByOrderByCreatedDateDesc(pageable);
    }

    @Transactional
    public void resetPassword(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません (id=" + userId + ")"));

        LoginCredential credential = loginCredentialRepository.findById(userId)
                .orElseGet(() -> {
                    LoginCredential lc = new LoginCredential();
                    lc.setUser(user);
                    lc.setCreatedDate(LocalDate.now());
                    return lc;
                });

        String temporaryPassword = INITAL_PASSWORD;

        credential.setPasswordHash(PasswordUtil.hash(temporaryPassword));
        credential.setEditedDate(LocalDate.now());

        loginCredentialRepository.save(credential);
    }

}
