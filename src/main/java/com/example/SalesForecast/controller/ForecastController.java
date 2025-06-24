package com.example.SalesForecast.controller;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.SalesForecast.domain.api.PredictApiClient;
import com.example.SalesForecast.domain.api.PredictionRequestDto;
import com.example.SalesForecast.domain.api.PredictionResponseDto;
import com.example.SalesForecast.domain.inventory.service.InventoryService;
import com.example.SalesForecast.domain.product.entity.Product;
import com.example.SalesForecast.domain.product.service.ProductService;

@Controller
public class ForecastController {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductService productService;
    @Autowired

    private PredictApiClient predictApiClient;

    private List<LocalDate> windowDates(LocalDate orderDate) {
        DayOfWeek dow = orderDate.getDayOfWeek();
        if (dow != DayOfWeek.MONDAY && dow != DayOfWeek.THURSDAY) {
            throw new IllegalArgumentException("発注日は月曜または木曜でなければなりません");
        }
        return List.of(
                orderDate,
                orderDate.plusDays(1),
                orderDate.plusDays(2));
    }

    private LocalDate nextValidOrderDate(LocalDate base) {
        while (base.getDayOfWeek() != DayOfWeek.MONDAY &&
                base.getDayOfWeek() != DayOfWeek.THURSDAY) {
            base = base.plusDays(1);
        }
        return base;
    }

    @GetMapping("/forecast")
    public String showForecastPage(
            Model model,
            @RequestParam(value = "orderDate", required = false) String orderDate,
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

        model.addAttribute("orderDate", orderDate);
        LocalDate od = (orderDate == null)
                ? nextValidOrderDate(LocalDate.now()) // ← 修正！
                : LocalDate.parse(orderDate, DateTimeFormatter.ISO_DATE);
        List<LocalDate> dates = windowDates(od);

        List<Product> beers = productService.getAvailableProducts();
        List<PredictionRequestDto> reqs = new ArrayList<>();
        for (Product p : beers) {
            String dbName = p.getName();
            for (LocalDate d : dates) {
                reqs.add(new PredictionRequestDto(
                        d.format(DateTimeFormatter.ISO_DATE),
                        dbName));
            }
        }

        // API レスポンス取得＆ビールごと合計
        List<PredictionResponseDto> responses = predictApiClient.fetchPredictions(reqs);
        Map<String, BigDecimal> sumByBeer = responses.stream()
                .filter(r -> r.getError() == null)
                .collect(Collectors.groupingBy(
                        PredictionResponseDto::getBeerType,
                        Collectors.mapping(
                                PredictionResponseDto::getPredictedSales,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        // items リスト組み立て
        class Item {
            public String beerName;
            public int currentInventory;
            public BigDecimal predictedSales;
            public int orderQty;
            public String error;
        }
        List<Item> items = new ArrayList<>();
        for (Product p : beers) {
            Item it = new Item();
            it.beerName = p.getName();
            it.currentInventory = inventoryService
                    .getInventoryByProductId(p.getId())
                    .getStockQuantity();

            String apiCode = predictApiClient.toApiCode(it.beerName);

            if (sumByBeer.containsKey(apiCode)) {
                BigDecimal totalPred = sumByBeer.get(apiCode);
                it.predictedSales = totalPred;

                BigDecimal diff = totalPred.subtract(BigDecimal.valueOf(it.currentInventory));
                it.orderQty = diff.compareTo(BigDecimal.ZERO) <= 0
                        ? 0
                        : diff.setScale(0, RoundingMode.CEILING).intValue();
            } else {
                // 予測失敗（APIに含まれていない＝エラーだった）
                it.error = "unsupported beer type: " + it.beerName;
            }
            items.add(it);
        }
        model.addAttribute("forecastItems", items);
        model.addAttribute("orderDate", od.format(DateTimeFormatter.ISO_DATE));

        return "forecast";
    }
}
