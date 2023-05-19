package com.example.coap_demo.server;


import org.eclipse.californium.core.CoapServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.coap_demo.server.resource.AddSensorResource;
import com.example.coap_demo.server.resource.PerformanceResource;
import com.example.coap_demo.server.resource.SensorResource;

@SpringBootApplication
public class CoapDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoapDemoApplication.class, args);
    }

    @Bean
    public CoapServer coapServer(SensorResource sensorResource, AddSensorResource addSensorResource, PerformanceResource performanceResource) {
        CoapServer coapServer = new CoapServer();
        coapServer.add(sensorResource);
        coapServer.add(addSensorResource);
        coapServer.add(performanceResource);
        coapServer.start();
        return coapServer;
    }

    @Bean
    public SensorResource sensorResource() {
        return new SensorResource("sensor");
    }

    @Bean
    public AddSensorResource addSensorResource() {
        return new AddSensorResource("addSensor");
    }

    @Bean
    public PerformanceResource performanceResource() {
        return new PerformanceResource("performance");
    }
}
