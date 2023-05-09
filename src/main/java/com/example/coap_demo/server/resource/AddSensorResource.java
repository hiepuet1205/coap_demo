package com.example.coap_demo.server.resource;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddSensorResource extends CoapResource {

    private final static Logger logger = LoggerFactory.getLogger(AddSensorResource.class);

    private static Integer NUM_SENSORS_CURRENTLY = 0;

    public AddSensorResource(String name) {
        super(name);

        setObservable(true); // enable observing
        setObserveType(CoAP.Type.CON); // configure the notification type to CONs

        getAttributes().setTitle("Sensor create Observable Resource");
        getAttributes().setObservable(); // mark observable in the Link-Format
    }

    @Override
    public void handleGET(CoapExchange exchange) {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("numberSensorCurrently", NUM_SENSORS_CURRENTLY);

        // Trả về chuỗi JSON làm nội dung của phản hồi
        exchange.respond(CoAP.ResponseCode.CONTENT, jsonObject.toString(), MediaTypeRegistry.APPLICATION_JSON);
    }

    @Override
    public void handlePUT(CoapExchange exchange) {

        System.out.println("test create ");

        // Nhận payload từ yêu cầu CoAP
        byte[] payload = exchange.getRequestPayload();

        // Chuyển đổi chuỗi payload thành đối tượng JSONObject
        String payloadStr = new String(payload);

        JSONObject jsonObject = new JSONObject(payloadStr);

        // Lấy giá trị từ đối tượng JSONObject
        Integer numberNewSensor = jsonObject.getInt("number");

        // cap nhat number sensor currently
        NUM_SENSORS_CURRENTLY += numberNewSensor;

        changed();

        Response response = new Response(CoAP.ResponseCode.CHANGED);
        response.setPayload("Đã nhận và xử lý payload thành công".getBytes());
        exchange.respond(response);
    }
}