package com.example.SalesForecast.controller;

import com.example.SalesForecast.domain.sales.service.SalesService;
import com.example.SalesForecast.service.FilterOptionService;
import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "salesDate,desc") String sort,
            Model model,
            HttpSession session) {
        // ログイン情報
        String loginUserName = (String) session.getAttribute("username");
        String loginUserEmail = (String) session.getAttribute("email");
        String loginUserRole = (String) session.getAttribute("login_user_role");
        String loginUserStatus = (String) session.getAttribute("login_user_status");

        if (loginUserName == null) {
            return "access-denied";
        }
        if (!loginUserRole.equals("admin") && !loginUserRole.equals("user") && !loginUserRole.equals("viewer")) {
            return "authority-denied";
        }

        model.addAttribute("current_username", loginUserName);
        model.addAttribute("current_email", loginUserEmail);
        model.addAttribute("login_user_role", loginUserRole);
        model.addAttribute("login_user_status", loginUserStatus);

        // フィルタ用リスト
        model.addAttribute("years", salesService.getAvailableYears());
        model.addAttribute("months", salesService.getAvailableMonths());
        model.addAttribute("weatherOptions", filterOptionService.getAllWeatherConditions());
        model.addAttribute("brandOptions", filterOptionService.getAllProductNames());

        // ── ソート文字列 (field,dir) を分解 ──
        String[] parts = sort.split(",");
        String sortField = parts[0];
        String sortDir = parts.length > 1 ? parts[1] : "asc";

        Sort sd = Sort.by(sortField);
        sd = "asc".equalsIgnoreCase(sortDir) ? sd.ascending() : sd.descending();

        // ── ページング＋ソートで取得 ──
        Pageable pageable = PageRequest.of(page, size, sd);
        Page<Map<String, Object>> pageData = salesService
                .getFilteredSalesWeatherRecords(
                        year, month, weather, brand, volume, sales, rain, temp,
                        pageable);

        // ── ページャー用計算 ──
        int totalPages = pageData.getTotalPages();
        int current = pageData.getNumber();
        int startPage = Math.max(0, current - 5);
        int endPage = Math.min(totalPages - 1, current + 5);

        // ── モデル登録 ──
        model.addAttribute("salesWeatherList", pageData.getContent());
        model.addAttribute("currentPage", current);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // ── ソート状態保持 ──
        model.addAttribute("currentSort", sort);

        // ── フィルタ再選択状態保持 ──
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedWeather", weather);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("selectedVolume", volume);
        model.addAttribute("selectedSales", sales);
        model.addAttribute("selectedRain", rain);
        model.addAttribute("selectedTemp", temp);

        return "weather-sales";
    }
}
