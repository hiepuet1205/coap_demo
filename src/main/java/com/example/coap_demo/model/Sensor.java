package com.example.coap_demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class Sensor {
    private String id;
    private Double value;

    private Boolean isRunning = true;

    public Sensor(String id, Double value){
        this.id = id;
        this.value = value;
    }
}
