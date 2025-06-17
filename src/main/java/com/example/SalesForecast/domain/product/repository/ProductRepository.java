package com.example.SalesForecast.domain.product.repository;

import com.example.SalesForecast.domain.product.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Product findByJanCode(String JanCode);
}
