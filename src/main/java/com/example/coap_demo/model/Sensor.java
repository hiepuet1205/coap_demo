package com.example.coap_demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Data @NoArgsConstructor @AllArgsConstructor
public class Sensor {
    private String id;
    private Double value;
    private Boolean isRunning = true;
    private String lastUpdate;

    public Sensor(String id, Double value){
        this.id = id;
        this.value = value;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String lastUpdate = now.format(formatter);

        this.lastUpdate = lastUpdate;
    }

    public JSONObject sensorToJsonObject(){
        // Tạo đối tượng JSON
        JSONObject jsonObject = new JSONObject();

        // Thiết lập thuộc tính
        jsonObject.put("id", this.id);
        jsonObject.put("value", this.getValue());
        jsonObject.put("isRunning", this.isRunning);
        jsonObject.put("lastUpdate", this.lastUpdate);

        return jsonObject;
    }

    public void randomTemperature() {
        Random random = new Random();
        Double minTemperature = -10.0;
        Double maxTemperature = 40.0;
        Double temperatureRange = maxTemperature - minTemperature;
        Double generatedTemperature = random.nextDouble() * temperatureRange + minTemperature;
        this.setValue(generatedTemperature);
    }
}
