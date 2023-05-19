package com.example.coap_demo.server;


import com.example.coap_demo.server.resource.AddSensorResource;
import com.example.coap_demo.server.resource.SensorResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.Resource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CoapDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoapDemoApplication.class, args);
    }

    @Bean
    public CoapServer coapServer(SensorResource sensorResource, AddSensorResource addSensorResource) {
        CoapServer coapServer = new CoapServer();
<<<<<<< HEAD

        // Đăng ký các tài nguyên (resources) vào máy chủ CoAP
        coapServer.add(new SensorResource("sensor"));

        coapServer.add(new AddSensorResource("addSensor"));

        // Khởi động máy chủ CoAP
=======
        coapServer.add(sensorResource);
        coapServer.add(addSensorResource);
>>>>>>> 7fc1b347d5239db6584ea6157cc6831c11a3ed14
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
}
