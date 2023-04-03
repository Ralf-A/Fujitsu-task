package com.ralf.tt.trial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:file:./data/WeatherData";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "password";

    public static void main(String[] args) {
       new DataParser();
        new ScheduledJob().ScheduledJob();
        SpringApplication.run(Main.class, args);

    }


}
