package com.example.SalesForecast.domain.api;

import java.math.BigDecimal;

public class PredictionResponseDto {
    private String date;
    private String beerType;
    private BigDecimal predictedSales;
    private String error;

    public PredictionResponseDto() {
    }

    public PredictionResponseDto(String date, String beerType, BigDecimal predictedSales, String error) {
        this.date = date;
        this.beerType = beerType;
        this.predictedSales = predictedSales;
        this.error = error;
    }

    // getters / setters...
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

    public BigDecimal getPredictedSales() {
        return predictedSales;
    }

    public void setPredictedSales(BigDecimal predictedSales) {
        this.predictedSales = predictedSales;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
