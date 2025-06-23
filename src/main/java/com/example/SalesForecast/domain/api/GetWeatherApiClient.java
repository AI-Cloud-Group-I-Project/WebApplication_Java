package com.example.SalesForecast.domain.api;

import com.example.SalesForecast.domain.weather.dto.WeatherDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Map;

@Component
public class GetWeatherApiClient {

    /* Azure Functions エンドポイント（関数キー付き） */
    private static final String API_URL = "https://weather-forecast-class-i.azurewebsites.net/api/get_weather"
            + "?code=KR-r5f1TQt_31zpPaE4k6zq4QlTQWy76gdh8cVxIHPECAzFuD7sApg==";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public WeatherDto fetchWeather(LocalDate date) {

        /* (1) POST ボディ JSON を組み立て */
        Map<String, String> payload = Map.of("date", date.toString());
        String jsonBody;
        try {
            jsonBody = mapper.writeValueAsString(payload);
        } catch (Exception e) {
            return errorDto(date, "json-serialize-fail");
        }

        /* (2) HTTP リクエスト */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<String> resp;
        try {
            resp = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
        } catch (RestClientException ex) {
            return errorDto(date, "http-error");
        }

        if (!resp.getStatusCode().is2xxSuccessful()) {
            return errorDto(date, "status-" + resp.getStatusCode().value());
        }

        /* (3) JSON → WeatherDto 変換 */
        try {
            /*
             * 返却 JSON 例 (想定)
             * {
             * "date": "2025-06-24",
             * "temperature": 25.3,
             * "precipitation": 0.0,
             * "weather": "曇天"
             * }
             */
            Map<String, Object> m = mapper.readValue(
                    resp.getBody(),
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                    });

            float temp = ((Number) m.get("temperature")).floatValue();
            float precip = ((Number) m.get("precipitation")).floatValue();
            String weather = (String) m.get("weather");

            return new WeatherDto(date.toString(), temp, precip, weather);

        } catch (Exception e) {
            return errorDto(date, "json-parse-fail");
        }
    }

    /** エラー用 DTO を生成 */
    private WeatherDto errorDto(LocalDate date, String msg) {
        return new WeatherDto(date.toString(), 0f, -1f, msg);
    }
}
