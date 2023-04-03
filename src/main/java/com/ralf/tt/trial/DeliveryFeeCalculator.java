package com.ralf.tt.trial;

import java.util.HashMap;
import java.util.Map;

public class DeliveryFeeCalculator {
    /**
     * Class to calculate delivery prices
     * Return base fee
     */

    // Regional base fees (RBF) for different cities and vehicle types
    private static final Map<String, Map<String, Double>> RBF_MAP = Map.of(
            "Tallinn", Map.of("Car", 4.0, "Scooter", 3.5, "Bike", 3.0),
            "Tartu", Map.of("Car", 3.5, "Scooter", 3.0, "Bike", 2.5),
            "Pärnu", Map.of("Car", 3.0, "Scooter", 2.5, "Bike", 2.0));

    // Extra fee for temperature
    private static final Map<String, Double> ATEF_MAP = Map.of(
            "-10.0", 1.0, "0.0", 0.5);
    // Air temperature < -10, +1€;
    // -10 < x < 0, +0.5€

    // Extra fee for wind speed
    private static final Map<String, Double> WSEF_MAP = Map.of(
            "0.0", 0.0, "10.0", 0.5);
    // Wind speed 20 < x < 10, +0.5€


    private static final Map<String, Double> WPEF_MAP = new HashMap<>() {{
        put("Heavy snow shower", 1.0);
        put("Light snow shower", 1.0);
        put("Moderate snow shower", 1.0);
        put("Light sleet", 1.0);
        put("Moderate sleet", 1.0);
        put("Light snowfall", 1.0);
        put("Moderate snowfall", 1.0);
        put("Heavy snowfall", 1.0);
        put("Blowing snow", 1.0);
        put("Drifting snow", 1.0);
        put("Light shower", 0.5);
        put("Moderate shower", 0.5);
        put("Heavy shower", 0.5);
        put("Light rain", 0.5);
        put("Moderate rain", 0.5);
        put("Heavy rain", 0.5);}};
    // Snow or sleet +1€
    // Rain +0.5€

    // Method to calculate the delivery fee based on input parameters and weather data
    public static double calculateDeliveryFee(String city, String vehicleType, double temperature, double windSpeed, String weatherPhenomenon) {

        // Calculate the regional base fee based on vehicle and city
        double rbf = RBF_MAP.get(city).get(vehicleType);

        // Calculate extra fees for weather conditions, if applicable
        double atef = 0.0;
        double wsef = 0.0;
        double wpef = 0.0;

        if (vehicleType.equals("Scooter") || vehicleType.equals("Bike")) {
            // Extra fee based on air temperature
            String temperatureKey = ATEF_MAP.keySet().stream()
                    .filter(key -> temperature < Double.parseDouble(key))
                    .findFirst()
                    .orElse(null);
            if (temperatureKey != null) {
                atef = ATEF_MAP.get(temperatureKey);
            }

            // Extra fee based on wind speed
            if (vehicleType.equals("Bike")) {
                if (windSpeed > 20.0) {
                    throw new IllegalArgumentException("Usage of selected vehicle type is forbidden");
                    // Wind speed < 20, "Usage of selected vehicle type is forbidden"
                }
                String windSpeedKey = WSEF_MAP.keySet().stream()
                        .filter(key -> windSpeed >= Double.parseDouble(key))
                        .findFirst()
                        .orElse(null);
                if (windSpeedKey != null) {
                    wsef = WSEF_MAP.get(windSpeedKey);
                }
            }

            // Extra fee based on weather phenomenon
            if (WPEF_MAP.containsKey(weatherPhenomenon)) {
                wpef = WPEF_MAP.get(weatherPhenomenon);
            } else if (weatherPhenomenon.equals("Glaze") ||
                    weatherPhenomenon.equals("Hail") ||
                    weatherPhenomenon.equals("Thunder") ||
                    weatherPhenomenon.equals("Thunderstorm")) {
                throw new IllegalArgumentException("Usage of selected vehicle type is forbidden");
                // Glaze, hail, thunder and bike or scooter - "Usage of selected vehicle type is forbidden"
            }
        }

        // Calculate the total delivery fee
        double fee = rbf + atef + wsef + wpef;
        return fee;
    }
}