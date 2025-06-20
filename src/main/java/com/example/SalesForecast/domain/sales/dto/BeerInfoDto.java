package com.example.SalesForecast.domain.sales.dto;

public class BeerInfoDto {
    private final String beer_name;
    private final int quantity;
    private final int price;
    private final String jan_code;

    public BeerInfoDto(String beer_name, int quantity, int price, String jan_code) {
        this.beer_name = beer_name;
        this.quantity = quantity;
        this.price = price;
        this.jan_code = jan_code;
    }

    public String getBeer_name() {
        return beer_name;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public String getJan_code() {
        return jan_code;
    }
}
