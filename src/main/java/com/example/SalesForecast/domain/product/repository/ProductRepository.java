package com.example.SalesForecast.domain.product.repository;

import com.example.SalesForecast.domain.product.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Product findByJanCode(String JanCode);

    @Query("SELECT DISTINCT p.name FROM Product p ORDER BY p.name")
    List<String> findDistinctProductNames();

    List<Product> findByStatusId(Integer statusId);

    List<Product> findAllByStatus_Id(Integer statusId);

    Page<Product> findAllByOrderByIdAsc(Pageable pageable);

    Page<Product> findAllByOrderByIdDesc(Pageable pageable);

}
