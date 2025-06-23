package com.example.SalesForecast.domain.sales.service;

import com.example.SalesForecast.domain.api.GetWeatherApiClient;
import com.example.SalesForecast.domain.inventory.service.InventoryService;
import com.example.SalesForecast.domain.product.entity.Product;
import com.example.SalesForecast.domain.product.entity.ProductPriceHistory;
import com.example.SalesForecast.domain.product.repository.ProductPriceHistoryRepository;
import com.example.SalesForecast.domain.product.service.ProductService;
import com.example.SalesForecast.domain.sales.dto.BeerInfoDto;
import com.example.SalesForecast.domain.sales.entity.Sales;
import com.example.SalesForecast.domain.sales.repository.SalesRepository;
import com.example.SalesForecast.domain.user.entity.User;
import com.example.SalesForecast.domain.weather.dto.WeatherDto;
import com.example.SalesForecast.domain.weather.entity.Weather;
import com.example.SalesForecast.domain.weather.service.WeatherService;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalesService {

    private final SalesRepository salesRepository;
    private final WeatherService weatherService;
    private final GetWeatherApiClient getWeatherApiClient;
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final ProductPriceHistoryRepository historyRepository;

    public SalesService(SalesRepository salesRepository,
            WeatherService weatherService,
            GetWeatherApiClient getWeatherApiClient,
            ProductService productService,
            InventoryService inventoryService,
            ProductPriceHistoryRepository historyRepository) {
        this.salesRepository = salesRepository;
        this.weatherService = weatherService;
        this.getWeatherApiClient = getWeatherApiClient;
        this.productService = productService;
        this.inventoryService = inventoryService;
        this.historyRepository = historyRepository;
    }

    // 選択した条件で絞り込み
    public List<Map<String, Object>> fetchFilteredRecords(
            Integer year,
            Integer month,
            String weatherCondition,
            String brandName,
            String volumeRange,
            String salesRange,
            String rainRange,
            String tempRange) {
        List<Sales> filteredSales = salesRepository.findAll().stream()

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
                .collect(Collectors.toList());

        // ── (B) 必要な productId と salesDate の範囲を取得 ──
        List<Integer> productIds = filteredSales.stream()
                .map(s -> s.getProduct().getId())
                .distinct()
                .collect(Collectors.toList());
        LocalDate minDate = filteredSales.stream()
                .map(Sales::getSalesDate)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
        LocalDate maxDate = filteredSales.stream()
                .map(Sales::getSalesDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        // ── (C) 履歴を一括フェッチ ──
        List<ProductPriceHistory> histories = historyRepository.findByProductIdsAndDateRange(productIds, minDate,
                maxDate);

        // ── (D) Java 側で「effectiveFrom/changedAt 降順」で最適レコードを選抜 ──
        // キーを "productId_salesDate" の文字列にして Map に詰める
        Map<String, BigDecimal> priceMap = new HashMap<>();
        for (Sales sale : filteredSales) {
            String key = sale.getProduct().getId() + "_" + sale.getSalesDate();
            ProductPriceHistory best = histories.stream()
                    .filter(h -> h.getProduct().getId().equals(sale.getProduct().getId()))
                    .filter(h -> !h.getEffectiveFrom().isAfter(sale.getSalesDate()))
                    .filter(h -> h.getEffectiveTo() == null
                            || !h.getEffectiveTo().isBefore(sale.getSalesDate()))
                    .sorted(Comparator
                            .comparing(ProductPriceHistory::getEffectiveFrom).reversed()
                            .thenComparing(
                                    Comparator.comparing(ProductPriceHistory::getChangedAt).reversed()))
                    .findFirst()
                    .orElse(null);
            priceMap.put(key, (best != null ? best.getPrice() : BigDecimal.ZERO));
        }

        // ── (E) 価格を priceMap から取り出して Map<String,Object> に詰める ──
        List<Map<String, Object>> records = new ArrayList<>();
        for (Sales sale : filteredSales) {
            Map<String, Object> rec = new HashMap<>();
            rec.put("salesDate", sale.getSalesDate());
            rec.put("brand", sale.getProduct().getName());
            rec.put("quantity", sale.getQuantity());

            String key = sale.getProduct().getId() + "_" + sale.getSalesDate();
            BigDecimal priceDec = priceMap.get(key);
            int price = priceDec.intValue();
            int total = price * sale.getQuantity();

            rec.put("totalSales", total);
            rec.put("totalSalesFormatted", String.format("%,d", total));
            rec.put("weather", sale.getWeather().getWeatherCondition());
            rec.put("precipitation", sale.getWeather().getPrecipitation());
            rec.put("temperature", sale.getWeather().getAverageTemperature());

            records.add(rec);
        }

        return records;
    }

    /**
     * ページング＋ソート対応版
     */
    public Page<Map<String, Object>> getFilteredSalesWeatherRecords(
            Integer year, Integer month,
            String weatherCondition, String brandName,
            String volumeRange, String salesRange,
            String rainRange, String tempRange,
            Pageable pageable) {

        // 1) フィルタ＋DTO化
        List<Map<String, Object>> all = fetchFilteredRecords(
                year, month, weatherCondition,
                brandName, volumeRange, salesRange,
                rainRange, tempRange);

        // 2) ソート指定があれば in‐memory でソート
        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            String field = order.getProperty();
            boolean asc = order.isAscending();

            all.sort((m1, m2) -> {
                Object o1 = m1.get(field);
                Object o2 = m2.get(field);
                if (o1 == null && o2 == null)
                    return 0;
                if (o1 == null)
                    return 1;
                if (o2 == null)
                    return -1;
                // 明示的に unchecked キャストして Comparable とみなす
                @SuppressWarnings("unchecked")
                Comparable<Object> c1 = (Comparable<Object>) o1;
                @SuppressWarnings("unchecked")
                Comparable<Object> c2 = (Comparable<Object>) o2;
                return asc
                        ? c1.compareTo(c2)
                        : c2.compareTo(c1);
            });
        }

        // 3) PageImpl でスライス
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());
        List<Map<String, Object>> content = start > end ? Collections.emptyList() : all.subList(start, end);

        return new PageImpl<>(content, pageable, all.size());
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
                    WeatherDto dto = getWeatherApiClient.fetchWeather(date);

                    if (dto.hasError()) {
                        throw new IllegalStateException("Weather API failed: " + dto.getWeather());
                    }

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
