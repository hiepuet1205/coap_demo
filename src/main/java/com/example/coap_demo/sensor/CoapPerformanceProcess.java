package com.example.coap_demo.sensor;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.json.JSONObject;

public class CoapPerformanceProcess {
    private static final String COAP_SERVER_URL = "coap://localhost:5683/performance";

    public static void main(String[] args) {
        CoapClient client = new CoapClient(COAP_SERVER_URL);

        // Tạo đối tượng Timer
        Timer timer = new Timer();

        // Tạo đối tượng TimerTask để định nghĩa công việc cần thực hiện
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
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
        };

        // Lên lịch cho công việc chạy sau mỗi 1 giây (1000 milliseconds)
        timer.scheduleAtFixedRate(task, 0, 5000);
    }
}
