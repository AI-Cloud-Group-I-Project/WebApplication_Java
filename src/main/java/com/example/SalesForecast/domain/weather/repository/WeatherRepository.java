package com.example.SalesForecast.domain.weather.repository;

import com.example.SalesForecast.domain.weather.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

public interface WeatherRepository extends JpaRepository<Weather, Integer> {
    // Optinal : 値が存在するかもしれないし、しないかもしれない。
    Optional<Weather> findByDate(LocalDate date);

    @Query("SELECT DISTINCT w.weatherCondition FROM Weather w ORDER BY w.weatherCondition")
    List<String> findDistinctWeatherConditions();
}
