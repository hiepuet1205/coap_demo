package com.example.coap_demo.client;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.Request;
<<<<<<< HEAD
import org.eclipse.californium.elements.exception.ConnectorException;
=======
>>>>>>> 7fc1b347d5239db6584ea6157cc6831c11a3ed14
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoapClientManager {
    private final static Logger logger = LoggerFactory.getLogger(CoapClientManager.class);
<<<<<<< HEAD
        private int NUMBER_SENSOR = 0;
        private static final String COAP_SERVER_ADD_URL = "coap://localhost:5683/addSensor"; // Địa chỉ URI của server CoAP

        private CoapPostSensorProcess coapPostSensorProcess;

        private void getObserve(){
            CoapClient clientManager = new CoapClient(COAP_SERVER_ADD_URL);

            logger.info("OBSERVING ... {}", COAP_SERVER_ADD_URL);

            Request request = Request.newGet().setURI(COAP_SERVER_ADD_URL).setObserve();
            request.setConfirmable(true);

            CoapObserveRelation relation = clientManager.observe(request, new CoapHandler() {
                public void onLoad(CoapResponse response) {
                    String content = response.getResponseText();

                    JSONObject jsonObject = stringToJsonObject(content);

                    // Lấy giá trị từ đối tượng JSONObject
                    int newNumber = jsonObject.getInt("numberSensorCurrently");

                    if (newNumber != NUMBER_SENSOR) {
                        int numberCreateSensor = newNumber - NUMBER_SENSOR;
                        NUMBER_SENSOR = newNumber;

                        CoapPostSensorProcess coapPostSensorProcess = new CoapPostSensorProcess();
                        if (numberCreateSensor > 1) {
                            coapPostSensorProcess.handleCreateManySensor(numberCreateSensor);
                        } else if ( numberCreateSensor == 1) {
                            coapPostSensorProcess.handleCreateOneSensor();
                        }
                    }

                    logger.info("Notification Response Pretty Print: \n{}", Utils.prettyPrint(response));
                    logger.info("NOTIFICATION return the number sensor: " + jsonObject);
                }

                public void onError() {
                    logger.error("OBSERVING FAILED");
                }
            });
        }

        private JSONObject stringToJsonObject(String content){
            JSONObject jsonObject = new JSONObject(content);
            return jsonObject;
        }

}


=======
    private static int NUMBER_SENSOR = 0;
    private static final String COAP_SERVER_ADD_URL = "coap://localhost:5683/addSensor"; // Địa chỉ URI của server CoAP
    private CoapPostSensorProcess coapPostSensorProcess;

    public static void main(String[] args) {
        CoapClient clientManager = new CoapClient(COAP_SERVER_ADD_URL);

        logger.info("OBSERVING ... {}", COAP_SERVER_ADD_URL);

        Request request = Request.newGet().setURI(COAP_SERVER_ADD_URL).setObserve();
        request.setConfirmable(true);

        CoapObserveRelation relation = clientManager.observe(request, new CoapHandler() {
            public void onLoad(CoapResponse response) {
                String content = response.getResponseText();

                JSONObject jsonObject = stringToJsonObject(content);

                // Lấy giá trị từ đối tượng JSONObject
                int newNumber = jsonObject.getInt("numberSensorCurrently");

                if (newNumber != NUMBER_SENSOR) {
                    int numberCreateSensor = newNumber - NUMBER_SENSOR;
                    NUMBER_SENSOR = newNumber;

                    CoapPostSensorProcess coapPostSensorProcess = new CoapPostSensorProcess();
                    if (numberCreateSensor > 1) {
                        coapPostSensorProcess.handleCreateManySensor(numberCreateSensor);
                    } else if ( numberCreateSensor == 1) {
                        coapPostSensorProcess.handleCreateOneSensor();
                    }
                }

                logger.info("Notification Response Pretty Print: \n{}", Utils.prettyPrint(response));
                logger.info("NOTIFICATION return the number sensor: " + jsonObject);
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
>>>>>>> 7fc1b347d5239db6584ea6157cc6831c11a3ed14
