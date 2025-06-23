package com.example.SalesForecast.domain.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PredictApiClient {

    @Value("${predict.api.url}")
    private String FUNCTION_URL;

    @Value("${predict.api.key}")
    private String FUNCTION_KEY;

    // API が受け取るコード → DB 登録名
    private static final Map<String, String> TO_DB_NAME = Map.of(
            "Pale_Ale", "ペールエール",
            "Lager", "ラガー",
            "IPA", "IPA",
            "White_Beer", "ホワイトビール",
            "Dark_Beer", "黒ビール",
            "Fruit_Beer", "フルーツビール");

    // DB 登録名 → API コード
    private static final Map<String, String> DB_NAME_TO_CODE = TO_DB_NAME.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public String toApiCode(String dbName) {
        return DB_NAME_TO_CODE.get(dbName);
    }

    public List<PredictionResponseDto> fetchPredictions(List<PredictionRequestDto> reqs) {

        /* (ⅰ) DB 名 → API 名へ変換 */
        List<Map<String, String>> payload = reqs.stream()
                .map(r -> Map.of(
                        "date", r.getDate(),
                        "beer_type", Optional.ofNullable(toApiCode(r.getBeerType()))
                                .orElse(r.getBeerType()) // 変換失敗時はそのまま
                ))
                .collect(Collectors.toList());

        /* (ⅱ) JSON 文字列生成 */
        String jsonBody;
        try {
            jsonBody = mapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize payload", e);
        }

        /* (ⅲ) HTTP POST */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = FUNCTION_URL + "?code=" + FUNCTION_KEY;
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (!resp.getStatusCode().is2xxSuccessful()) {
            // 非 2xx はまとめて error に
            return reqs.stream()
                    .map(r -> new PredictionResponseDto(
                            r.getDate(), r.getBeerType(), null,
                            "HTTP error " + resp.getStatusCode().value()))
                    .collect(Collectors.toList());
        }

        /* (ⅳ) JSON → DTO */
        List<Map<String, Object>> rawList;
        try {
            rawList = mapper.readValue(
                    resp.getBody(),
                    new TypeReference<>() {
                    });
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse prediction JSON", e);
        }

        /* (ⅴ) レスポンスを DTO へ変換 */
        return rawList.stream().map(m -> {
            String date = (String) m.get("date");
            String beerType = (String) m.get("beer_type");

            if (m.containsKey("error")) {
                return new PredictionResponseDto(
                        date, beerType, null, (String) m.get("error"));
            } else {
                BigDecimal pred = new BigDecimal(String.valueOf(m.get("predicted_sales")));
                return new PredictionResponseDto(
                        date, beerType, pred, null);
            }
        }).collect(Collectors.toList());
    }
}
