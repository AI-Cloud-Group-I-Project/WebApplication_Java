package com.example.SalesForecast.domain.sales.service;

import com.example.SalesForecast.domain.forecast.client.ForecastApiClient;
import com.example.SalesForecast.domain.inventory.service.InventoryService;
import com.example.SalesForecast.domain.product.entity.Product;
import com.example.SalesForecast.domain.product.service.ProductService;
import com.example.SalesForecast.domain.sales.dto.BeerInfoDto;
import com.example.SalesForecast.domain.sales.entity.Sales;
import com.example.SalesForecast.domain.sales.repository.SalesRepository;
import com.example.SalesForecast.domain.user.entity.User;
import com.example.SalesForecast.domain.weather.dto.WeatherDto;
import com.example.SalesForecast.domain.weather.entity.Weather;
import com.example.SalesForecast.domain.weather.service.WeatherService;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalesService {

    private final SalesRepository salesRepository;
    private final WeatherService weatherService;
    private final ForecastApiClient forecastApiClient;
    private final ProductService productService;
    private final InventoryService inventoryService;

    public SalesService(SalesRepository salesRepository,
            WeatherService weatherService,
            ForecastApiClient forecastApiClient,
            ProductService productService,
            InventoryService inventoryService) {
        this.salesRepository = salesRepository;
        this.weatherService = weatherService;
        this.forecastApiClient = forecastApiClient;
        this.productService = productService;
        this.inventoryService = inventoryService;
    }

    // 選択した条件で絞り込み
    public List<Map<String, Object>> getFilteredSalesWeatherRecords(
            Integer year,
            Integer month,
            String weatherCondition,
            String brandName,
            String volumeRange,
            String salesRange,
            String rainRange,
            String tempRange) {
        return salesRepository.findAll().stream()

                // 年フィルタ
                .filter(sale -> year == null || sale.getSalesDate().getYear() == year)

                // 月フィルタ
                .filter(sale -> month == null || sale.getSalesDate().getMonthValue() == month)

                // 天気フィルタ
                .filter(sale -> weatherCondition == null || weatherCondition.isBlank()
                        || sale.getWeather().getWeatherCondition().equals(weatherCondition))

                // 銘柄フィルタ
                .filter(sale -> brandName == null || brandName.isBlank()
                        || sale.getProduct().getName().equals(brandName))

                // 本数(volume)フィルタ
                .filter(sale -> {
                    if (volumeRange == null || volumeRange.isBlank()) {
                        return true;
                    }
                    String[] rangeParts = volumeRange.split("～");
                    int quantity = sale.getQuantity();
                    if (rangeParts.length == 2) {
                        int min = Integer.parseInt(rangeParts[0]);
                        int max = Integer.parseInt(rangeParts[1].replaceAll("\\D", ""));
                        return quantity >= min && quantity <= max;
                    }
                    if (volumeRange.contains("以上")) {
                        int min = Integer.parseInt(volumeRange.replaceAll("\\D", ""));
                        return quantity >= min;
                    }
                    return true;
                })

                // 売上金額(sales)フィルタ
                .filter(sale -> {
                    if (salesRange == null || salesRange.isBlank())
                        return true;

                    int price = Optional.ofNullable(sale.getProduct().getPrice())
                            .map(Double::intValue).orElse(0);
                    int total = price * sale.getQuantity();

                    if (salesRange.contains("以上")) {
                        int min = Integer.parseInt(salesRange.replaceAll("\\D", ""));
                        return total >= min;
                    }

                    String[] parts = salesRange.split("～");
                    if (parts.length == 2) {
                        int min = Integer.parseInt(parts[0]);
                        int max = Integer.parseInt(parts[1].replaceAll("\\D", ""));
                        return total >= min && total <= max;
                    }
                    return true;
                })

                // 降水量(rain)フィルタ
                .filter(sale -> {
                    if (rainRange == null || rainRange.isBlank())
                        return true;

                    double precipitation = sale.getWeather().getPrecipitation();

                    if (rainRange.equals("0.1未満")) {
                        return precipitation < 0.1;
                    }

                    if (rainRange.contains("以上")) {
                        double min = Double.parseDouble(rainRange.replaceAll("[^\\d.]", ""));
                        return precipitation >= min;
                    }

                    String[] parts = rainRange.split("～");
                    if (parts.length == 2) {
                        double min = Double.parseDouble(parts[0]);
                        double max = Double.parseDouble(parts[1]);
                        return precipitation >= min && precipitation <= max;
                    }

                    return true;
                })

                // 平均気温(temp)フィルタ
                .filter(sale -> {
                    if (tempRange == null || tempRange.isBlank())
                        return true;

                    double temperature = sale.getWeather().getAverageTemperature();

                    if (tempRange.contains("以下")) {
                        double max = Double.parseDouble(tempRange.replaceAll("[^\\d.-]", ""));
                        return temperature < max;
                    }

                    if (tempRange.contains("以上")) {
                        double min = Double.parseDouble(tempRange.replaceAll("[^\\d.-]", ""));
                        return temperature >= min;
                    }

                    String[] parts = tempRange.split("～");
                    if (parts.length == 2) {
                        double min = Double.parseDouble(parts[0]);
                        double max = Double.parseDouble(parts[1]);
                        return temperature >= min && temperature <= max;
                    }

                    return true;
                })

                // DTOマップに変換
                .map(this::toMap)
                .collect(Collectors.toList());
    }

    // 画面表示用 Map 組み立て
    private Map<String, Object> toMap(Sales sale) {
        Map<String, Object> record = new HashMap<>();
        record.put("salesDate", sale.getSalesDate());
        record.put("brand", sale.getProduct().getName());
        record.put("quantity", sale.getQuantity());

        // 金額計算
        int price = Optional.ofNullable(sale.getProduct().getPrice())
                .map(Double::intValue)
                .orElse(0);
        int totalSales = price * sale.getQuantity();
        // カンマ区切りの文字列を作成
        String formattedTotalSales = String.format("%,d", totalSales);
        record.put("totalSales", formattedTotalSales);

        Weather weatherEntity = sale.getWeather();
        record.put("weather", weatherEntity.getWeatherCondition());
        record.put("precipitation", weatherEntity.getPrecipitation());
        record.put("temperature", weatherEntity.getAverageTemperature());

        return record;
    }

    /**
     * フォーム用：利用可能な年リスト
     */
    public List<Integer> getAvailableYears() {
        return salesRepository.findAll().stream()
                .map(sale -> sale.getSalesDate().getYear())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * フォーム用：利用可能な月リスト
     */
    public List<Integer> getAvailableMonths() {
        return Arrays.stream(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 })
                .collect(Collectors.toList());
    }

    public List<BeerInfoDto> getBeerInfoByDate(LocalDate date, List<Product> allProducts) {
        List<Sales> salesList = salesRepository.findBySalesDate(date);
        Map<Integer, Integer> qtyByProductId = salesList.stream()
                .collect(Collectors.toMap(
                        s -> s.getProduct().getId(),
                        Sales::getQuantity,
                        Integer::sum));

        return allProducts.stream()
                .filter(p -> qtyByProductId.getOrDefault(p.getId(), 0) > 0)
                .map(p -> new BeerInfoDto(
                        p.getId(),
                        p.getName(),
                        qtyByProductId.getOrDefault(p.getId(), 0),
                        p.getPrice().intValue(),
                        p.getJanCode()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void registerSales(LocalDate date, User user,
            List<Integer> productIds,
            List<Integer> quantities) {

        // 1) Weather を取得 or 作成
        Weather weather = weatherService.getByDate(date)
                .orElseGet(() -> {
                    WeatherDto dto = forecastApiClient.fetchWeather(date);
                    Weather w = new Weather();
                    w.setDate(LocalDate.parse(dto.getDate())); // String → LocalDate に変換
                    w.setAverageTemperature(dto.getTemperature());
                    w.setPrecipitation(dto.getPrecipitation());
                    w.setWeatherCondition(dto.getWeather());

                    // ここでエンティティを保存して返す
                    return weatherService.save(w);
                });

        // ２）各行を登録 or 更新 ＋ 在庫更新
        for (int i = 0; i < productIds.size(); i++) {
            Integer pid = productIds.get(i);
            Integer newQty = quantities.get(i);

            // 既存 Sales を探す
            Optional<Sales> opt = salesRepository
                    .findBySalesDateAndProduct_Id(date, pid);

            if (opt.isPresent()) {
                // --- 更新する場合 ---
                Sales existing = opt.get();
                int oldQty = existing.getQuantity();
                int delta = newQty - oldQty;

                // 在庫を差分で調整
                if (delta > 0) {
                    inventoryService.deductStock(pid, delta);
                } else if (delta < 0) {
                    inventoryService.addStock(pid, -delta); // 必要なら在庫を戻すメソッドを用意
                }

                existing.setQuantity(newQty);
                existing.setEditedDate(LocalDate.now());
                salesRepository.save(existing);

            } else {
                // --- 新規挿入する場合 ---
                // 在庫をまるごと引く
                inventoryService.deductStock(pid, newQty);

                Sales sale = new Sales();
                sale.setProduct(productService.getById(pid));
                sale.setUser(user);
                sale.setWeather(weather);
                sale.setQuantity(newQty);
                sale.setSalesDate(date);
                sale.setCreatedDate(LocalDate.now());
                sale.setEditedDate(LocalDate.now());
                salesRepository.save(sale);
            }
        }
    }
}
