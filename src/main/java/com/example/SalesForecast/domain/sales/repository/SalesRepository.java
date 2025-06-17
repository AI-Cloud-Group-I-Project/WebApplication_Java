package com.example.SalesForecast.domain.sales.repository;

import com.example.SalesForecast.domain.sales.entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesRepository extends JpaRepository<Sales, Integer> {
}
