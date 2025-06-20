package com.example.SalesForecast.domain.sales.repository;

import com.example.SalesForecast.domain.sales.entity.Sales;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesRepository extends JpaRepository<Sales, Integer> {
    List<Sales> findBySalesDate(LocalDate salesDate);

    Optional<Sales> findBySalesDateAndProduct_Id(LocalDate salesDate, Integer productId);

}
