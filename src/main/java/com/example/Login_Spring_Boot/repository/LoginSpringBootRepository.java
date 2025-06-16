package com.example.Login_Spring_Boot.repository;

import com.example.Login_Spring_Boot.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

// LoginSpringBootRepository.java
public interface LoginSpringBootRepository extends JpaRepository<Users, Integer> {
    Users findByUserName(String name); //SELECT * FROM users WHERE UserName = name
}
