package com.example.oneday.util;

import com.example.oneday.datamodel.Offsets;
import com.example.oneday.repository.OffsetsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;

@Service
public class OffsetsUtil {

    private final OffsetsRepository offsetsRepository;

    public OffsetsUtil(OffsetsRepository offsetsRepository) {
        this.offsetsRepository = offsetsRepository;
    }

    public void loadOffsets() {
        try (InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("datasets/offsets.json")) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readValue(in, JsonNode.class);
            String jsonString = mapper.writeValueAsString(jsonNode);
            jsonString = jsonString.replace("offset", "tempOffset");
            Offsets[] offsetsArray = mapper.readValue(jsonString, Offsets[].class);
            offsetsRepository.saveAll(Arrays.stream(offsetsArray).toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
