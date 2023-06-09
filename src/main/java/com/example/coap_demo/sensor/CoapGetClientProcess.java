package com.example.coap_demo.sensor;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;

public class CoapGetClientProcess {
    private static final String COAP_SERVER_URL = "coap://localhost:5683/addSensor"; // Địa chỉ URI của server CoAP

    private final static Logger logger = LoggerFactory.getLogger(CoapGetClientProcess.class);

    public static void main(String[] args) throws ConnectorException, IOException {
        CoapClient client = new CoapClient(COAP_SERVER_URL);

        logger.info("OBSERVING ... {}", COAP_SERVER_URL);

        Request request = Request.newGet().setURI(COAP_SERVER_URL).setObserve();
        request.setConfirmable(true);

        CoapObserveRelation relation = client.observe(request, new CoapHandler() {

            public void onLoad(CoapResponse response) {
                String content = response.getResponseText();
                logger.info("Notification Response Pretty Print: \n{}", Utils.prettyPrint(response));
                logger.info("NOTIFICATION Body: " + content);

                try (FileOutputStream out = new FileOutputStream("src/main/resources/output.json")) {
                    out.write(response.getPayload());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void onError() {
                logger.error("OBSERVING FAILED");
            }
        });

        // Observes the coap resource for 1000 seconds then the observing relation is
        // deleted
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.error("CANCELLATION.....");
        relation.proactiveCancel();

    }
}
