package com.example.SalesForecast.controller;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.SalesForecast.domain.product.entity.Product;
import com.example.SalesForecast.domain.product.service.ProductService;
import com.example.SalesForecast.domain.sales.service.SalesService;
import com.example.SalesForecast.domain.user.entity.User;
import com.example.SalesForecast.domain.user.service.UserService;
import com.example.SalesForecast.domain.sales.dto.BeerInfoDto;

@Controller
public class SalesController {

    private final SalesService salesService;
    private final ProductService productService;
    private final UserService userService;

    public SalesController(SalesService salesService,
            ProductService productService,
            UserService userService) {
        this.salesService = salesService;
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/record")
    public String showSalesRecordPage(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day,
            Model model,
            HttpSession session) {

        model.addAttribute("current_username", session.getAttribute("username"));
        model.addAttribute("current_email", session.getAttribute("email"));

        LocalDate targetDate = (year != null && month != null && day != null)
                ? LocalDate.of(year, month, day)
                : LocalDate.now();

        List<BeerInfoDto> beerList = salesService.getBeerInfoByDate(targetDate,
                productService.getAllProducts());

        model.addAttribute("beer_info_list", beerList);

        model.addAttribute("selectedYear", targetDate.getYear());
        model.addAttribute("selectedMonth", targetDate.getMonthValue());
        model.addAttribute("selectedDay", targetDate.getDayOfMonth());

        List<Product> availableProducts = productService.getProductsByStatusId(1);
        model.addAttribute("availableProducts", availableProducts);

        return "admin-record";
    }

    @PostMapping("/record")
    public String registerSales(
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam Integer day,
            @RequestParam("productId") List<Integer> productIds,
            @RequestParam("quantity") List<Integer> quantities,
            HttpSession session) {

        LocalDate date = LocalDate.of(year, month, day);
        String email = (String) session.getAttribute("email");
        User user = userService
                .getUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        salesService.registerSales(date, user, productIds, quantities);
        return "redirect:/record?year=" + year + "&month=" + month + "&day=" + day;
    }
}