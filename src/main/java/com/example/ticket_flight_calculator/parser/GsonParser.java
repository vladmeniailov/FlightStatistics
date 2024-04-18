package com.example.ticket_flight_calculator.parser;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileReader;

@Slf4j
@Component
public class GsonParser<T> {
    public T parse(String fileName, Class<T> clazz) {
        try (FileReader fileReader = new FileReader(fileName)) {
            return new Gson().fromJson(fileReader, clazz);
        } catch (Exception e) {
            log.error("Error reading json \"{}\"", fileName);
            throw new RuntimeException(e.getMessage());
        }
    }
}
