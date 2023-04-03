package com.ralf.tt.trial;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@RestController
public class Endpoint {
    @GetMapping("/delivery-fee")
    public ResponseEntity<Object> calculateDeliveryFee(
            @RequestParam String city,
            @RequestParam String vehicleType
    ){
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/weather_db", "sa", "password");
            String query;
            switch(city) {
                case "Tallinn":
                    query = "SELECT AIR_TEMPERATURE, WIND_SPEED, WEATHER_PHENOMENON FROM WEATHER_DATA WHERE WMO_CODE = 26038 AND OBSERVATION_TIMESTAMP = (SELECT MAX(OBSERVATION_TIMESTAMP) FROM WEATHER_DATA WHERE WMO_CODE = 26038)";
                    break;
                case "Tartu":
                    query = "SELECT AIR_TEMPERATURE, WIND_SPEED, WEATHER_PHENOMENON FROM WEATHER_DATA WHERE WMO_CODE = 26242 AND OBSERVATION_TIMESTAMP = (SELECT MAX(OBSERVATION_TIMESTAMP) FROM WEATHER_DATA WHERE WMO_CODE = 26242)";
                    break;
                case "Pärnu":
                    query = "SELECT AIR_TEMPERATURE, WIND_SPEED, WEATHER_PHENOMENON FROM WEATHER_DATA WHERE WMO_CODE = 41803 AND OBSERVATION_TIMESTAMP = (SELECT MAX(OBSERVATION_TIMESTAMP) FROM WEATHER_DATA WHERE WMO_CODE = 41803)";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid city: " + city);
            }
            double temperature = 0;
            double windSpeed = 0;
            String phenomenon = null;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        temperature = rs.getDouble("AIR_TEMPERATURE");
                        windSpeed = rs.getDouble("WIND_SPEED");
                        phenomenon = rs.getString("WEATHER_PHENOMENON");
                    } else {
                        throw new RuntimeException("No weather data found for city: " + city);
                    }
                }
            } finally {
                conn.close();
            }

            return ResponseEntity.ok(DeliveryFeeCalculator.calculateDeliveryFee(city, vehicleType,
                    temperature, windSpeed, phenomenon));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid input parameters: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
}





