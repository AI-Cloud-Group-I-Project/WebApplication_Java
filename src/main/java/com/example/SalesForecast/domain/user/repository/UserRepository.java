package com.example.SalesForecast.domain.user.repository;

import com.example.SalesForecast.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    // emailで検索するメソッド
    User findByEmail(String email);

    List<User> findAllByOrderByCreatedDateAsc(); // 昇順

    List<User> findAllByOrderByCreatedDateDesc(); // 降順
}
