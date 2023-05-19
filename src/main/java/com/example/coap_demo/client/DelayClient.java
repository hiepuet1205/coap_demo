package com.example.coap_demo.client;

import com.example.coap_demo.model.Sensor;
import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

import static com.example.coap_demo.client.CoapPostSensorProcess.SENSOR_LIST_URL;

public class DelayClient {
    private final static Logger logger = LoggerFactory.getLogger(DelayClient.class);
    private static final String COAP_SERVER_DELAY_PACKET_URL = "coap://localhost:5683/delayClient"; // Địa chỉ URI của server CoAP
    private CoapClient sensorListClient = new CoapClient(SENSOR_LIST_URL);

    public static void main(String[] args) {
        CoapClient delayClient = new CoapClient(COAP_SERVER_DELAY_PACKET_URL);

        logger.info("OBSERVING ... {}", COAP_SERVER_DELAY_PACKET_URL);

        Request request = Request.newGet().setURI(COAP_SERVER_DELAY_PACKET_URL).setObserve();
        request.setConfirmable(true);

        CoapObserveRelation relation = delayClient.observe(request, new CoapHandler() {
            public void onLoad(CoapResponse response) {

            }
            public void onError() {
                logger.error("OBSERVING FAILED");
            }
        });

        // Observes the coap resource for 1000 seconds then the observing relation is deleted
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.error("CANCELLATION.....");
        relation.proactiveCancel();
    }

    private static JSONObject stringToJsonObject(String content){
        JSONObject jsonObject = new JSONObject(content);
        return jsonObject;
    }

    private void postToServer() {
        Request request = new Request(CoAP.Code.POST);

        request.setConfirmable(true);

        long timeStart = System.currentTimeMillis();

        request.setPayload(longToBytes(timeStart));

        logger.info("Request post time start:\n{}", Utils.prettyPrint(request));

        CoapResponse coapResponse = null;

        try {
            coapResponse = sensorListClient.advanced(request);

            //Pretty print for the received response
            logger.info("Response Pretty Print: \n{}", Utils.prettyPrint(coapResponse));

            //The "CoapResponse" message contains the response.
            String text = coapResponse.getResponseText();
            logger.info("Payload: {}", text);
            logger.info("Message ID: " + coapResponse.advanced().getMID());
            logger.info("Token: " + coapResponse.advanced().getTokenString());
        } catch (ConnectorException | IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        postToServer();
    }

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }

}
