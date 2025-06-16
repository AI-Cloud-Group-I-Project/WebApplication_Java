// package com.hopandcraft.forecastsystem.domain.forecast.client;

// import com.hopandcraft.forecastsystem.dto.response.ForecastResponseDto;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Component;
// import org.springframework.web.client.RestTemplate;

// @Component
// public class ForecastApiClient {

//     private final RestTemplate restTemplate;
//     private final String forecastApiUrl = "https://your-azure-function-url/api/forecast";  // ←実際のURLに置換

//     public ForecastApiClient() {
//         this.restTemplate = new RestTemplate();
//     }

//     public ForecastResponseDto callForecastApi() {
//         ResponseEntity<ForecastResponseDto> response = restTemplate.getForEntity(forecastApiUrl, ForecastResponseDto.class);
//         return response.getBody();
//     }
// }
