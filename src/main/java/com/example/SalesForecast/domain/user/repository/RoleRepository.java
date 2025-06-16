package com.example.SalesForecast.domain.user.repository;

import com.example.SalesForecast.domain.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    
}
