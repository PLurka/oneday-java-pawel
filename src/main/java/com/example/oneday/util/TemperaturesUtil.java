package com.example.oneday.util;

import com.example.oneday.datamodel.Temperatures;
import com.example.oneday.repository.TemperaturesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class TemperaturesUtil {

    private final TemperaturesRepository temperaturesRepository;

    public TemperaturesUtil(TemperaturesRepository temperaturesRepository) {
        this.temperaturesRepository = temperaturesRepository;
    }

    public void loadTemperatures() {
        try (InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("datasets/temperatures.json")) {
            ObjectMapper mapper = new ObjectMapper();
            Temperatures[] temperaturesArray = mapper.readValue(in, Temperatures[].class);
            temperaturesRepository.saveAll(Arrays.stream(temperaturesArray).collect(Collectors.toList()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
