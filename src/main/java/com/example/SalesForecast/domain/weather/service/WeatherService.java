package com.example.SalesForecast.domain.weather.service;

import com.example.SalesForecast.domain.weather.entity.Weather;
import com.example.SalesForecast.domain.weather.repository.WeatherRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class WeatherService {

    private final WeatherRepository weatherRepository;

    public WeatherService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public Optional<Weather> getByDate(LocalDate date) {
        return weatherRepository.findByDate(date);
    }

    public Weather save(Weather weather) {
        return weatherRepository.save(weather);
    }

}
