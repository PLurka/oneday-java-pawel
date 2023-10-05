package com.example.oneday.api;

import com.example.oneday.OnedayApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@SpringBootTest(classes = OnedayApplication.class)
public class TemperatureApiTest {

    @InjectMocks
    @Autowired
    TemperatureApi temperatureApi;

    @Test
    public void correctValuesReturned() {
        ResponseEntity<String> response = temperatureApi.getStandardMinimumOutdoorTemperature("AlÈs", "82 rue de Geneve");
        Assertions.assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        Assertions.assertEquals("-5.0", response.getBody());
    }

    @Test
    public void noResultForGivenInputReturned() {
        ResponseEntity<String> response = temperatureApi.getStandardMinimumOutdoorTemperature("FougÈres", "74 rue Isambard");
        Assertions.assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
        Assertions.assertEquals("No results for given input", response.getBody());
    }

    @Test
    public void noPostalCodeReturned() {
        ResponseEntity<String> response = temperatureApi.getStandardMinimumOutdoorTemperature("Tours", "6 quai Saint-Nicolas");
        Assertions.assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
        Assertions.assertEquals("No postal code provided for given address", response.getBody());
    }
}
