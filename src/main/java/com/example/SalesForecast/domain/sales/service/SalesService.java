package com.example.SalesForecast.domain.sales.service;

import com.example.SalesForecast.domain.product.entity.Product;
import com.example.SalesForecast.domain.sales.dto.BeerInfoDto;
import com.example.SalesForecast.domain.sales.entity.Sales;
import com.example.SalesForecast.domain.sales.repository.SalesRepository;
import com.example.SalesForecast.domain.weather.entity.Weather;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalesService {

    private final SalesRepository salesRepository;

    public SalesService(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
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

    public List<BeerInfoDto> getBeerInfoByDate(
            LocalDate date, List<Product> allProducts) {

        List<Sales> salesList = salesRepository.findBySalesDate(date);

        Map<Integer, Integer> qtyByProductId = salesList.stream()
                .collect(Collectors.toMap(
                        s -> s.getProduct().getId(),
                        Sales::getQuantity,
                        Integer::sum));

        return allProducts.stream()
                // ── ここで filter を追加 ──
                .filter(p -> qtyByProductId.getOrDefault(p.getId(), 0) > 0)
                .map(p -> new BeerInfoDto(
                        p.getName(),
                        qtyByProductId.getOrDefault(p.getId(), 0),
                        p.getPrice().intValue(),
                        p.getJanCode()))
                .collect(Collectors.toList());
    }

}
