package com.example.SalesForecast.domain.product.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(name = "jan_code", nullable = false)
    private String janCode;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private ProductStatus status;

    public Product() {
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public String getJanCode() {
        return janCode;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setJanCode(String janCode) {
        this.janCode = janCode;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }
}
