package com.example.ticket_flight_calculator.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ticket {
    private String origin;
    private String originName;
    private String destination;
    @SerializedName("destination_name")
    private String destinationName;
    @SerializedName("departure_date")
    private Date departureDate;
    @SerializedName("departure_time")
    private LocalTime departureTime;
    @SerializedName("arrival_date")
    private Date arrivalDate;
    @SerializedName("arrival_time")
    private LocalTime arrivalTime;
    private String carrier;
    private int stops;
    private int price;

    public long getDurationTime() {
        Duration duration = Duration.between(getDepartureTime(), getArrivalTime());
        return duration.toMinutes();
    }
}
