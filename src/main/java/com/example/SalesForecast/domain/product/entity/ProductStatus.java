package com.example.SalesForecast.domain.product.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_statuses")
public class ProductStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    public ProductStatus() {
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
