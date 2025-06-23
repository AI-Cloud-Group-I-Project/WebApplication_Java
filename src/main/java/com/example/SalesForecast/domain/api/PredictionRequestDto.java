package com.example.SalesForecast.domain.api;

public class PredictionRequestDto {
    private String date;
    private String beerType;
    // (Jackson や Thymeleaf でバインドできるよう、getter/setter を忘れずに)

    public PredictionRequestDto() {
    }

    public PredictionRequestDto(String date, String beerType) {
        this.date = date;
        this.beerType = beerType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBeerType() {
        return beerType;
    }

    public void setBeerType(String beerType) {
        this.beerType = beerType;
    }
}
