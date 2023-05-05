package com.example.coap_demo.server;


import com.example.coap_demo.server.resource.AddSensorResource;
import com.example.coap_demo.server.resource.SensorResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class CoapDemoApplication {

    public static void main(String[] args) throws ConnectorException, IOException {
        // Tạo một đối tượng CoapServer
        CoapServer coapServer = new CoapServer();

        // Đăng ký các tài nguyên (resources) vào máy chủ CoAP
        coapServer.add(new SensorResource("sensor"));

        coapServer.add(new AddSensorResource("addSensor"));

        // Khởi động máy chủ CoAP
        coapServer.start();
    }
}
