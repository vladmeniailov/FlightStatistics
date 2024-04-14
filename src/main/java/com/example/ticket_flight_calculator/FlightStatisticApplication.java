package com.example.ticket_flight_calculator;

import com.example.ticket_flight_calculator.service.FlightStatisticService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author Vlad Meniailov
 */

@SpringBootApplication
public class FlightStatisticApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightStatisticApplication.class, args);
	}

    @Bean
    CommandLineRunner commandLineRunner(FlightStatisticService flightStatisticService) {
        return args -> {
            flightStatisticService.calculateStatistics("src/main/resources/tickets.json");
        };
    }

}
