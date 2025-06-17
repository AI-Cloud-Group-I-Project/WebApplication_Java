package com.example.SalesForecast.domain.product.repository;

import com.example.SalesForecast.domain.product.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStatusRepository extends JpaRepository<ProductStatus, Integer> {
}
