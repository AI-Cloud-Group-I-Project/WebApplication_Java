package com.example.SalesForecast.domain.user.service;

import com.example.SalesForecast.domain.user.entity.User;
import com.example.SalesForecast.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 全ユーザ取得
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // IDで取得
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    // emailで取得
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    // 新規登録
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // 更新
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    // 削除（今後必要になれば）
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}
