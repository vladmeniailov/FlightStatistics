package com.example.ticket_flight_calculator.parser;

import com.example.ticket_flight_calculator.model.TicketsWrapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.time.LocalTime;
import java.util.Date;

@Slf4j
@Component
public class GsonParser<T> {
    public T parse(String fileName, Class<T> clazz) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class,new DateDeserializer())
                .registerTypeAdapter(LocalTime.class,new LocalTimeDeserializer())
                .create();

        try (FileReader fileReader = new FileReader(fileName)) {
            return gson.fromJson(fileReader, clazz);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
