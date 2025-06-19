package com.example.SalesForecast.service;

import com.example.SalesForecast.domain.product.repository.ProductRepository;
import com.example.SalesForecast.domain.weather.repository.WeatherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilterOptionService {

    private final ProductRepository productRepository;
    private final WeatherRepository weatherRepository;

    public FilterOptionService(ProductRepository productRepository, WeatherRepository weatherRepository) {
        this.productRepository = productRepository;
        this.weatherRepository = weatherRepository;
    }

    public List<String> getAllWeatherConditions() {
        return weatherRepository.findDistinctWeatherConditions();
    }

    public List<String> getAllProductNames() {
        return productRepository.findDistinctProductNames();
    }
}
