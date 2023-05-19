package com.example.coap_demo.server.resource;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelayPacketResource extends CoapResource {

    private final static Logger logger = LoggerFactory.getLogger(DelayPacketResource.class);

    private static long TIME_DELAY = 0;

    public DelayPacketResource(String name) {
        super(name);

        setObservable(true); // enable observing
        setObserveType(CoAP.Type.CON); // configure the notification type to CONs

        getAttributes().setTitle("Sensor create Observable Resource");
        getAttributes().setObservable(); // mark observable in the Link-Format
    }

    @Override
    public void handleGET(CoapExchange exchange) {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("TimeDelay", TIME_DELAY);

        // Trả về chuỗi JSON làm nội dung của phản hồi
        exchange.respond(CoAP.ResponseCode.CONTENT, jsonObject.toString(), MediaTypeRegistry.APPLICATION_JSON);
    }

    @Override
    public void handlePUT(CoapExchange exchange) {

        // Nhận payload từ yêu cầu CoAP
        byte[] payload = exchange.getRequestPayload();

        // Chuyển đổi chuỗi payload thành đối tượng JSONObject
        String payloadStr = new String(payload);

        JSONObject jsonObject = new JSONObject(payloadStr);

        // Lấy giá trị từ đối tượng JSONObject time start
        long timeStart = jsonObject.getLong("timeStart");

        // cap nhat number sensor currently
        long endTime = System.currentTimeMillis();

        TIME_DELAY = endTime - timeStart;

        logger.info("NOTIFICATION return the delay: " + TIME_DELAY);

        changed();

        Response response = new Response(CoAP.ResponseCode.CHANGED);
        response.setPayload("Đã nhận và xử lý payload thành công".getBytes());
        exchange.respond(response);
    }
}