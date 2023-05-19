package com.example.coap_demo.sensor;

import com.example.coap_demo.model.Sensor;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;


public class CoapUtils {
    public static final String SENSOR_LIST_URL = "coap://localhost:5683/sensor/"; // Địa chỉ URI của server CoAP

    private static final String COAP_SERVER_URL = "coap://localhost:5683/performance";
    private static Integer NUM_ID_SENSORS = 0;

    public void sendTimeStartClient() {
        CoapClient client = new CoapClient(COAP_SERVER_URL);

        Request request = new Request(CoAP.Code.POST);

        request.setConfirmable(true);

        // Tạo đối tượng JSON
        JSONObject jsonObject = new JSONObject();

        // Thiết lập thuộc tính
        jsonObject.put("timeStart", new Date(System.currentTimeMillis()).toString());

        request.setPayload(jsonObject.toString().getBytes());

        CoapResponse coapResp = null;
        try {
            coapResp = client.advanced(request);
        } catch (ConnectorException | IOException e) {
            e.printStackTrace();
        }
    }

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

