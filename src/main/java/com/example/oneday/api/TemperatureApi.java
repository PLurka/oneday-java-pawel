package com.example.oneday.api;

import com.example.oneday.datamodel.Temperatures;
import com.example.oneday.repository.OffsetsRepository;
import com.example.oneday.repository.TemperaturesRepository;
import com.example.oneday.util.OffsetsUtil;
import com.example.oneday.util.TemperaturesUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
public class TemperatureApi {

    public static final String API_KEY_PARAM = "&key=AIzaSyBJl9d2A7IYIj5Tuc2AFvroSOpeZVRtxd8";
    private final OffsetsUtil offsetsUtil;
    private final TemperaturesUtil temperaturesUtil;
    private final TemperaturesRepository temperaturesRepository;
    private final OffsetsRepository offsetsRepository;

    public TemperatureApi(final OffsetsUtil offsetsUtil, final TemperaturesUtil temperaturesUtil,
                          final TemperaturesRepository temperaturesRepository,
                          final OffsetsRepository offsetsRepository) {
        this.offsetsUtil = offsetsUtil;
        this.temperaturesUtil = temperaturesUtil;
        this.offsetsRepository = offsetsRepository;
        this.temperaturesRepository = temperaturesRepository;
    }

    @GetMapping("/load")
    public void loadData() {
        temperaturesUtil.loadTemperatures();
        offsetsUtil.loadOffsets();
    }

    @GetMapping("/temperature")
    public ResponseEntity<String> sayHello(@RequestParam(value = "city", defaultValue = "Bergerac") String city,
                                           @RequestParam(value = "street", defaultValue = "19 rue Jean Vilar") String street) {

        RestTemplate restTemplate = new RestTemplate();

        try {
            JSONObject firstResult = getFirstResult("https://maps.googleapis.com/maps/api/geocode/json?address=",
                    street, " ", city, restTemplate);
            JSONObject location = getLocation(firstResult);
            BigDecimal lng = location.getBigDecimal("lng");
            BigDecimal lat = location.getBigDecimal("lat");
            String postalCode = getPostalCode(firstResult);

            BigDecimal alt = getAltitude(restTemplate, lng, lat);

            Optional<Temperatures> temperature = temperaturesRepository.findByPostalCode(Integer.parseInt(postalCode));
            Optional<Integer> offset = offsetsRepository.findByAltitude(alt.intValue());

            return ResponseEntity.ok(String.valueOf(temperature.get().getTemperature() - offset.get()));
        } catch (NumberFormatException ex) {
            return ResponseEntity.status(500).body("No postal code provided for given address");
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(500).body("No results for given input");
        }
    }

    private static JSONObject getLocation(JSONObject firstResult) {
        JSONObject geometry = firstResult.getJSONObject("geometry");
        JSONObject location = geometry.getJSONObject("location");
        return location;
    }

    private static JSONObject getFirstResult(String link, String param1, String delimiter, String param2, RestTemplate restTemplate) {
        final String uri = link + param1 + delimiter + param2 + API_KEY_PARAM;
        String result = restTemplate.getForObject(uri, String.class);
        JSONObject jsonResult = new JSONObject(result);
        JSONArray results = jsonResult.getJSONArray("results");
        if (results.length() == 0) {
            throw new NoSuchElementException("Zero results");
        }
        return results.getJSONObject(0);
    }

    private static BigDecimal getAltitude(RestTemplate restTemplate, BigDecimal lng, BigDecimal lat) {
        JSONObject firstResult = getFirstResult("https://maps.googleapis.com/maps/api/elevation/json?locations=",
                lat.toString(), ",", lng.toString(), restTemplate);
        return firstResult.getBigDecimal("elevation");
    }

    private static String getPostalCode(JSONObject firstResult) {
        JSONArray addressComponents = firstResult.getJSONArray("address_components");
        JSONObject postalCodes = addressComponents.getJSONObject(addressComponents.length() - 1);
        String postalCode = (String) postalCodes.get("short_name");
        postalCode = postalCode.substring(0, 2);
        Integer.parseInt(postalCode);
        return postalCode;
    }
}
