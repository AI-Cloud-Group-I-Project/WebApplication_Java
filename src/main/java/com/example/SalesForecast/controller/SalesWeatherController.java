// 実績・天気データ確認ページ
package com.example.SalesForecast.controller;

import com.example.SalesForecast.domain.sales.entity.Sales;
import com.example.SalesForecast.domain.sales.repository.SalesRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class SalesWeatherController {

    private final SalesRepository salesRepository;

    public SalesWeatherController(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }

    @GetMapping("/performance/weather")
    public String showSalesWeather(Model model) {
        List<Sales> salesList = salesRepository.findAll();
        // salesの中でヒットしたものを受けとる
        // 天気の中でヒットしたものを受け取る
        // プロダクト(ビール)でヒットしたものを受け取る
        model.addAttribute("salesList", salesList);
        return "sales_weather"; // ここは後で作るsales_weather.htmlに対応
    }
}
