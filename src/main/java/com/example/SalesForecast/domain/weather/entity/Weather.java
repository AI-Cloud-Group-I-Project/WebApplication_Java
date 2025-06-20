package com.example.SalesForecast.domain.weather.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "weathers")
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "average_temperature", nullable = false)
    private Float averageTemperature;

    @Column(nullable = false)
    private Float precipitation;

    @Column(name = "weather_condition", nullable = false)
    private String weatherCondition;

    public Weather() {
    }

    public Integer getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Float getAverageTemperature() {
        return averageTemperature;
    }

    public Float getPrecipitation() {
        return precipitation;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setAverageTemperature(Float averageTemperature) {
        this.averageTemperature = averageTemperature;
    }

    public void setPrecipitation(Float precipitation) {
        this.precipitation = precipitation;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }
}
