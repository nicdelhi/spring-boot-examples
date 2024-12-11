package com.example.springboot.weather-service;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class WeatherApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }
}

@RestController
@RequestMapping("/app/weather")
class WeatherController {

    // In-memory database to store weather history
    private final Map<String, List<WeatherRequest>> weatherHistory = new HashMap<>();

    // OpenWeatherMap API key and endpoint
    private static final String API_KEY = "YOUR_API_KEY";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";

    @PostMapping
    public ResponseEntity<String> submitWeatherRequest(@RequestBody WeatherRequest weatherRequest) {
        String postcode = weatherRequest.getPostcode();

        // Save the request in history
        weatherHistory.computeIfAbsent(postcode, k -> new ArrayList<>()).add(weatherRequest);

        return ResponseEntity.ok("Weather request submitted successfully.");
    }

    @GetMapping("/history")
    public ResponseEntity<?> getWeatherHistory(@RequestParam String postalcode) {
        if (!weatherHistory.containsKey(postalcode)) {
            return ResponseEntity.status(404).body("No history found for postal code: " + postalcode);
        }

        return ResponseEntity.ok(weatherHistory.get(postalcode));
    }

    @GetMapping("/details")
    public ResponseEntity<?> getWeatherDetails(@RequestParam String postalcode) {
        RestTemplate restTemplate = new RestTemplate();
        String url = API_URL + "?zip=" + postalcode + ",us&appid=" + API_KEY;

        try {
            String response = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching weather details: " + e.getMessage());
        }
    }
}

class WeatherRequest {
    private String user;
    private String postcode;

    // Getters and setters
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}
