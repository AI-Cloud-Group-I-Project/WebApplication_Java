// src/main/java/com/example/SalesForecast/domain/sales/dto/BeerInfoDto.java
package com.example.SalesForecast.domain.sales.dto;

public class BeerInfoDto {
    private Integer productId; // ←追加
    private String beerName;
    private Integer quantity;
    private Integer price;
    private String janCode;

    public BeerInfoDto() {
    }

    // コンストラクタに productId を追加
    public BeerInfoDto(Integer productId,
            String beerName,
            Integer quantity,
            Integer price,
            String janCode) {
        this.productId = productId;
        this.beerName = beerName;
        this.quantity = quantity;
        this.price = price;
        this.janCode = janCode;
    }

    // --- 追加したフィールドの getter／setter ---
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    // --- 以下は既存の getter／setter ---
    public String getBeerName() {
        return beerName;
    }

    public void setBeerName(String beerName) {
        this.beerName = beerName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getJanCode() {
        return janCode;
    }

    public void setJanCode(String janCode) {
        this.janCode = janCode;
    }
}
