package com.example.coap_demo.server.resource;

import com.example.coap_demo.model.Sensor;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensorSubResource extends CoapResource {
    private final static Logger logger = LoggerFactory.getLogger(SensorResource.class);

    private Sensor sensor;

    public SensorSubResource(Sensor sensor) {
        super(sensor.getId());
        this.sensor = sensor;

        setObservable(true); // enable observing
        setObserveType(CoAP.Type.CON); // configure the notification type to CONs

        getAttributes().setTitle("Sensor " + this.sensor.getId() + " observes the temperature");
        getAttributes().setObservable(); // mark observable in the Link-Format
    }

    @Override
    public void handleGET(CoapExchange exchange) {

        logger.info("Request Pretty Print:\n{}", Utils.prettyPrint(exchange.advanced().getRequest()));

        // Chuyển đối tượng JsonObject thành chuỗi JSON
        String jsonString = this.sensor.sensorToJsonObject().toString();

        // Trả về chuỗi JSON làm nội dung của phản hồi
        exchange.respond(CoAP.ResponseCode.CONTENT, jsonString, MediaTypeRegistry.APPLICATION_JSON);
    }


    @Override
    public void handlePUT(CoapExchange exchange) {
        System.out.println("test");

        // Nhận payload từ yêu cầu CoAP
        byte[] payload = exchange.getRequestPayload();

        // Chuyển đổi chuỗi payload thành đối tượng JSONObject
        String payloadStr = new String(payload);

        JSONObject jsonObject = new JSONObject(payloadStr);

        // Lấy giá trị từ đối tượng JSONObject
        String id = jsonObject.getString("id");
        Double value = jsonObject.getDouble("value");
        Boolean isRunning = jsonObject.getBoolean("isRunning");

        Sensor newSensor = new Sensor(id, value, isRunning);
        this.sensor = newSensor;

        changed();

        SensorResource parentResource = (SensorResource) this.getParent();
        parentResource.updateSensorById(sensor);

        // Gửi phản hồi về cho yêu cầu CoAP
        Response response = new Response(CoAP.ResponseCode.CHANGED);
        response.setPayload("Đã nhận và update thành công".getBytes());
        exchange.respond(response);
    }
}
