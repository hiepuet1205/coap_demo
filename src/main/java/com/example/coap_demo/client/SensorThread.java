package com.example.coap_demo.client;

import com.example.coap_demo.model.Sensor;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.coap_demo.client.CoapPostSensorProcess.COAP_SERVER_URL;

public class SensorThread implements Runnable{
    private Sensor sensor;

    private final static Logger logger = LoggerFactory.getLogger(SensorThread.class);

    private CoapClient coapClient = new CoapClient(COAP_SERVER_URL);

    public SensorThread(Sensor sensor) {
        this.sensor = sensor;
    }

    private void postToServer() {
        // Tạo đối tượng Timer
        Timer timer = new Timer();

        // Tạo đối tượng TimerTask để định nghĩa công việc cần thực hiện
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // Chuyển đối tượng Sensor thành đối tượng JSONObject
                JSONObject sensorJson = new JSONObject();
                sensorJson.put("id", sensor.getId());
                sensorJson.put("value", sensor.getValue());

                Request request = new Request(CoAP.Code.POST);

                request.setConfirmable(true);

                request.setPayload(sensorJson.toString().getBytes());

                logger.info("Request Pretty Print:\n{}", Utils.prettyPrint(request));
                CoapResponse coapResp = null;
                try {

                    // đoạn này phải lấy value ra truoc
                    Double value = sensor.getValue();
                    if (value < 5) {
                        sensor.setValue(value + 1);
                    } else {
                        sensor.setIsRunning(false);
                    }

                    if(sensor.getIsRunning()){
                        coapResp = coapClient.advanced(request);

                        //Pretty print for the received response
                        logger.info("Response Pretty Print: \n{}", Utils.prettyPrint(coapResp));

                        //The "CoapResponse" message contains the response.
                        String text = coapResp.getResponseText();
                        logger.info("Payload: {}", text);
                        logger.info("Message ID: " + coapResp.advanced().getMID());
                        logger.info("Token: " + coapResp.advanced().getTokenString());
                    }


                } catch (ConnectorException | IOException e) {
                    e.printStackTrace();
                }
            }
        };

        // Lên lịch cho công việc chạy sau mỗi 10 giây (10000 milliseconds)
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    @Override
    public void run() {
        postToServer();
//        if(sensor.getIsRunning()){
//            postToServer();
//        }
    }
}
