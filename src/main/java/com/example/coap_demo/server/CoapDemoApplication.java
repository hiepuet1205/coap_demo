package com.example.coap_demo.server;


import com.example.coap_demo.server.resource.SensorResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.elements.exception.ConnectorException;

import java.io.IOException;
public class CoapDemoApplication {

    public static void main(String[] args) throws ConnectorException, IOException {
        // Tạo một đối tượng CoapServer
        CoapServer coapServer = new CoapServer();

        // Đăng ký các tài nguyên (resources) vào máy chủ CoAP
        coapServer.add(new SensorResource("sensor"));

        // Khởi động máy chủ CoAP
        coapServer.start();
    }
}
