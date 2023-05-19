package com.example.coap_demo.client;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.Request;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class DelayClient {
    private final static Logger logger = LoggerFactory.getLogger(DelayClient.class);
    private static final String COAP_SERVER_DELAY_PACKET_URL = "coap://localhost:5683/delayClient"; // Địa chỉ URI của server CoAP

    public static void main(String[] args) {
        CoapClient delayClient = new CoapClient(COAP_SERVER_DELAY_PACKET_URL);

        logger.info("OBSERVING ... {}", COAP_SERVER_DELAY_PACKET_URL);

        Request request = Request.newGet().setURI(COAP_SERVER_DELAY_PACKET_URL).setObserve();
        request.setConfirmable(true);

        CoapObserveRelation relation = delayClient.observe(request, new CoapHandler() {
            public void onLoad(CoapResponse response) {
                String content = response.getResponseText();

                JSONObject jsonObject = stringToJsonObject(content);

                // Lấy giá trị từ đối tượng JSONObject
                long timeDelay = jsonObject.getLong("TimeDelay");

                Date delay = new Date(timeDelay);

                logger.info("NOTIFICATION return the delay: " + delay);
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

}
