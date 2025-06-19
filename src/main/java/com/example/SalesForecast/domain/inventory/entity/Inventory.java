package com.example.SalesForecast.domain.inventory.entity;

import com.example.SalesForecast.domain.product.entity.Product;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventories")
public class Inventory {

    @Id
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    // 🔽 Productエンティティへの関連を追加（product_id でJoin）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    public Inventory() {
    }

    public Inventory(Integer productId, Integer stockQuantity, LocalDateTime updatedDate) {
        this.productId = productId;
        this.stockQuantity = stockQuantity;
        this.updatedDate = updatedDate;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    // 🔽 Getterを追加
    public Product getProduct() {
        return product;
    }
}
