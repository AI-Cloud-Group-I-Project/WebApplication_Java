package com.example.SalesForecast.domain.user.repository;

import com.example.SalesForecast.domain.user.entity.LoginCredential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginCredentialRepository extends JpaRepository<LoginCredential, Integer> {

}
