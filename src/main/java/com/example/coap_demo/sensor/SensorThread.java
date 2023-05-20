package com.example.coap_demo.sensor;

import static com.example.coap_demo.sensor.CoapUtils.SENSOR_LIST_URL;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.coap_demo.model.Sensor;

public class SensorThread implements Runnable {
    private Sensor sensor;
    private final static Logger logger = LoggerFactory.getLogger(SensorThread.class);
    private String SENSOR_URL;
    private CoapClient sensorListClient = new CoapClient(SENSOR_LIST_URL);
    private CoapClient sensorClient;

    public SensorThread(Sensor sensor) {
        this.sensor = sensor;
        this.SENSOR_URL = SENSOR_LIST_URL + this.sensor.getId();
        this.sensorClient = new CoapClient(SENSOR_URL);
    }

    private void getObserve() {
        Request request = Request.newGet().setURI(this.SENSOR_URL).setObserve();
        request.setConfirmable(true);

        CoapObserveRelation relation = sensorClient.observe(request, new CoapHandler() {
            public void onLoad(CoapResponse response) {
                String content = response.getResponseText();

                JSONObject jsonObject = stringToJsonObject(content);

                // Lấy giá trị từ đối tượng JSONObject
                Boolean isRunning = jsonObject.getBoolean("isRunning");

                sensor.setIsRunning(isRunning);

                logger.info("Notification Response Pretty Print: \n{}", Utils.prettyPrint(response));
                logger.info("NOTIFICATION Body: " + jsonObject);
            }

            public void onError() {
                logger.error("OBSERVING FAILED");
            }
        });
    }

    private void postToServer() {
        Request request = new Request(CoAP.Code.POST);

        request.setConfirmable(true);

        request.setPayload(sensor.sensorToJsonObject().toString().getBytes());

        logger.info("Request Pretty Print:\n{}", Utils.prettyPrint(request));
        CoapResponse coapResp = null;
        try {
            coapResp = sensorListClient.advanced(request);

            // Pretty print for the received response
            logger.info("Response Pretty Print: \n{}", Utils.prettyPrint(coapResp));

            // The "CoapResponse" message contains the response.
            String text = coapResp.getResponseText();
            logger.info("Payload: {}", text);
            logger.info("Message ID: " + coapResp.advanced().getMID());
            logger.info("Token: " + coapResp.advanced().getTokenString());
        } catch (ConnectorException | IOException e) {
            e.printStackTrace();
        }
    }

    private void continuousPutToServer() {
        // Tạo đối tượng Timer
        Timer timer = new Timer();

        // Tạo đối tượng TimerTask để định nghĩa công việc cần thực hiện
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Request request = new Request(CoAP.Code.PUT);

                request.setConfirmable(true);

                request.setPayload(sensor.sensorToJsonObject().toString().getBytes());

                logger.info("Request Pretty Print:\n{}", Utils.prettyPrint(request));
                CoapResponse coapResp = null;

                Boolean isRunning = sensor.getIsRunning();

                if (isRunning) {
                    try {
                        coapResp = sensorClient.advanced(request);

                        if (coapResp != null) {
                            // Pretty print for the received response
                            logger.info("Response Pretty Print: \n{}", Utils.prettyPrint(coapResp));

                            // The "CoapResponse" message contains the response.
                            String text = coapResp.getResponseText();
                            logger.info("Payload: {}", text);
                            logger.info("Message ID: " + coapResp.advanced().getMID());
                            logger.info("Token: " + coapResp.advanced().getTokenString());

                            sensor.randomTemperature();
                        }
                    } catch (ConnectorException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        // Lên lịch cho công việc chạy sau mỗi 1 giây (1000 milliseconds)
        timer.scheduleAtFixedRate(task, 0, 5000);
    }

    @Override
    public void run() {
        postToServer();
        getObserve();
        continuousPutToServer();
    }

    private JSONObject stringToJsonObject(String content) {
        JSONObject jsonObject = new JSONObject(content);

        return jsonObject;
    }
}
