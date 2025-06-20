package com.example.SalesForecast.domain.forecast.client;

import org.springframework.stereotype.Component;

import com.example.SalesForecast.domain.weather.dto.WeatherDto;

import java.time.LocalDate;

@Component
public class ForecastApiClient {
    public WeatherDto fetchWeather(LocalDate date) {

        // テスト用ダミーデータ返却
        return new WeatherDto(
                date.toString(),
                25.0f, // temperature
                0.0f, // precipitation
                "曇天" // weather
        );
    }
}