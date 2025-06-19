package com.example.SalesForecast.controller;

import com.example.SalesForecast.domain.sales.service.SalesService;
import com.example.SalesForecast.service.FilterOptionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SalesWeatherController {

    private final SalesService salesService;
    private final FilterOptionService filterOptionService;

    public SalesWeatherController(
            SalesService salesService,
            FilterOptionService filterOptionService) {
        this.salesService = salesService;
        this.filterOptionService = filterOptionService;
    }

    @GetMapping("/sales-weather")
    public String showSalesWeatherPage(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String weather,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String volume,
            @RequestParam(required = false) String sales,
            @RequestParam(required = false) String rain,
            @RequestParam(required = false) String temp,
            Model model,
            HttpSession session) {
        // ログイン情報
        model.addAttribute("current_username", session.getAttribute("username"));
        model.addAttribute("current_email", session.getAttribute("email"));

        // フィルタ用リスト
        model.addAttribute("years", salesService.getAvailableYears());
        model.addAttribute("months", salesService.getAvailableMonths());
        model.addAttribute("weatherOptions", filterOptionService.getAllWeatherConditions());
        model.addAttribute("brandOptions", filterOptionService.getAllProductNames());

        // 絞り込み結果
        model.addAttribute("salesWeatherList",
                salesService.getFilteredSalesWeatherRecords(
                        year, month, weather, brand, volume, sales, rain, temp));

        // 再選択状態保持
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedWeather", weather);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("selectedVolume", volume);
        model.addAttribute("selectedSales", sales);
        model.addAttribute("selectedRain", rain);
        model.addAttribute("selectedTemp", temp);

        return "admin-weather-sales";
    }
}
