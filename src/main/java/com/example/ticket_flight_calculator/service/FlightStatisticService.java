package com.example.ticket_flight_calculator.service;

import com.example.ticket_flight_calculator.model.Ticket;
import com.example.ticket_flight_calculator.model.TicketsWrapper;
import com.example.ticket_flight_calculator.parser.GsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightStatisticService {

    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yy");
    private final GsonParser<TicketsWrapper> parser;

    public void calculateStatistics(String pathTicketsJson) {
        TicketsWrapper ticketsWrapper = readJson(pathTicketsJson);
        List<Ticket> filteredTickets = getFilteredTickets(ticketsWrapper);
        Map<String, Long> minFlightTimeByCompanies = getMinFlightTimeByCompanies(filteredTickets);
        double averagePrice = getAveragePrice(filteredTickets);
        double medianPrice = getMedianPrice(filteredTickets);
        double difference = Math.abs(averagePrice - medianPrice);

        System.out.println("Минимальное время перелета Владивосток - Тель-Авив:");
        minFlightTimeByCompanies.forEach((company, minutes) -> {
            long hours = minutes / 60;
            long remainingMinutes = minutes % 60;
            System.out.println(
                    "Компанией \"" + company + "\" - " + minutes + " минут" + " (" + hours + " час. " + remainingMinutes + " минут.)");
        });
        System.out.println("\nСредняя цена полета между городами Владивосток и Тель-Авив - " + averagePrice + " руб.");
        System.out.println("Медиана полета между городами Владивосток и Тель-Авив - " + medianPrice + " руб.");
        System.out.println("\nРазница между средней ценой и медианой - " + difference + " руб.");
    }

    private static List<Ticket> getFilteredTickets(TicketsWrapper ticketsWrapper) {
        return ticketsWrapper.getTickets()
                .stream()
                .filter(ticket -> ticket.getOrigin().equals("VVO")
                        && ticket.getDestination().equals("TLV"))
                .collect(Collectors.toList());
    }

    private TicketsWrapper readJson(String filePath) {
        return parser.parse(filePath, TicketsWrapper.class);
    }

    private double getAveragePrice(List<Ticket> tickets) {
        return tickets.stream()
                .mapToDouble(Ticket::getPrice)
                .average()
                .orElseThrow(() -> new IllegalArgumentException("Cannot calculate average for an empty list"));
    }

    private double getMedianPrice(List<Ticket> tickets) {
        List<Integer> sortedPrices = tickets.stream()
                .map(Ticket::getPrice)
                .sorted()
                .toList();
        int middle = sortedPrices.size() / 2;
        if (sortedPrices.size() % 2 == 1) {
            return sortedPrices.get(middle);
        } else {
            return (sortedPrices.get(middle - 1) + sortedPrices.get(middle)) / 2.0;
        }
    }

    private Map<String, Long> getMinFlightTimeByCompanies(List<Ticket> tickets) {
        return tickets.stream()
                .collect(Collectors.groupingBy(Ticket::getCarrier,
                        Collectors.mapping(this::getDurationTime,
                                Collectors.minBy(Comparator.naturalOrder()))))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().isPresent())
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get()));
    }

    private long getDurationTime(Ticket ticket) {
        LocalDate dateDeparture = LocalDate.parse(ticket.getDepartureDate(), DATE_FORMAT);
        LocalTime timeDeparture = LocalTime.parse(ticket.getDepartureTime(), TIME_FORMAT);
        LocalDateTime dateTimeDeparture = LocalDateTime.of(dateDeparture, timeDeparture);

        LocalDate dateArrival = LocalDate.parse(ticket.getArrivalDate(), DATE_FORMAT);
        LocalTime timeArrival = LocalTime.parse(ticket.getArrivalTime(), TIME_FORMAT);
        LocalDateTime dateTimeArrival = LocalDateTime.of(dateArrival, timeArrival);

        Duration flightTime = Duration.between(dateTimeDeparture, dateTimeArrival);
        return flightTime.toMinutes();
    }
}
