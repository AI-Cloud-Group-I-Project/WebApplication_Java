package com.example.SalesForecast.domain.user.repository;

import com.example.SalesForecast.domain.user.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    // emailで検索するメソッド
    User findByEmail(String email);

    Page<User> findAllByOrderByCreatedDateAsc(Pageable pageable);

    Page<User> findAllByOrderByCreatedDateDesc(Pageable pageable);
}
