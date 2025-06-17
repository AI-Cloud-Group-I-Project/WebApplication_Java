package com.example.SalesForecast.domain.weather.repository;

import com.example.SalesForecast.domain.weather.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRepository extends JpaRepository<Weather, Integer> {
}
