package com.example.coap_demo.client;

import com.example.coap_demo.model.Sensor;

public class CoapPostSensorProcess {
    public static final String SENSOR_LIST_URL = "coap://localhost:5683/sensor/"; // Địa chỉ URI của server CoAP

    private static Integer NUM_ID_SENSORS = 0;

//    public static void main(String[] args) throws ConnectorException, IOException {
//        // Khởi tạo và chạy các luồng Sensor
//        for (int i = 0; i < 1; i++) {
//            Sensor sensor = new Sensor("Sensor00" + (i + 1), 0.0); // Thay đổi giá trị cho từng Sensor tại đây
//            // Tạo và khởi chạy luồng Sensor
//            Thread sensorThread = new Thread(new SensorThread(sensor));
//            sensorThread.start();
//        }
//    }

    public void handleCreateOneSensor() {
        Sensor sensor = new Sensor("Sensor00" + (NUM_ID_SENSORS + 1), 0.0);
        NUM_ID_SENSORS++;
        Thread sensorThread = new Thread(new SensorThread(sensor));
        sensorThread.start();
    }

    public void handleCreateManySensor( int numberSensor) {
        for (int i = 0; i < numberSensor; i++) {
            Sensor sensor = new Sensor("Sensor00" + (i + 1), 0.0); // Thay đổi giá trị cho từng Sensor tại đây
            NUM_ID_SENSORS++;
            // Tạo và khởi chạy luồng Sensor
            Thread sensorThread = new Thread(new SensorThread(sensor));
            sensorThread.start();
        }
    }
}

