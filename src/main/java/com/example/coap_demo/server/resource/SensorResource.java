package com.example.coap_demo.server.resource;


import com.example.coap_demo.model.Sensor;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SensorResource extends CoapResource {
    private final static Logger logger = LoggerFactory.getLogger(SensorResource.class);

    private List<Sensor> sensors = new ArrayList<>();

    public SensorResource(String name) {
        super(name);

        setObservable(true); // enable observing
        setObserveType(CoAP.Type.CON); // configure the notification type to CONs

        getAttributes().setTitle("Temperature Observable Resource");
        getAttributes().setObservable(); // mark observable in the Link-Format
    }

    public boolean checkSensorExistsById(List<Sensor> sensors, String id) {
        for (Sensor sensor : sensors) {
            if (sensor.getId().equals(id)) {
                return true; // Nếu tìm thấy Sensor có ID trùng khớp, trả về true
            }
        }
        return false; // Nếu không tìm thấy Sensor nào có ID trùng khớp, trả về false
    }

    public void updateSensorById(List<Sensor> sensors, String id, Double value) {
        for (int i = 0; i < sensors.size(); i++) {
            Sensor sensor = sensors.get(i);
            if (sensor.getId().equals(id)) {
                // Nếu tìm thấy Sensor có ID trùng khớp, thực hiện cập nhật
                Sensor newSensor = new Sensor(id, value);
                sensors.set(i, newSensor);
                break; // Thoát khỏi vòng lặp sau khi cập nhật Sensor
            }
        }
    }


    @Override
    public void handleGET(CoapExchange exchange) {

        logger.info("Request Pretty Print:\n{}", Utils.prettyPrint(exchange.advanced().getRequest()));

        // Lấy params từ yêu cầu CoAP
//        Request request = exchange.advanced().getRequest();
//        String param1 = request.getOptions().getUriQuery().get(0);
////        String param2 = request.getOptions().getUriQuery().getFirst("param2");
//
//        System.out.println(param1);

        // Tạo đối tượng JSON
        JSONObject jsonObject = new JSONObject();

        // Thiết lập thuộc tính "success" với giá trị true
        jsonObject.put("success", true);

        // Thiết lập thuộc tính "result" với giá trị số sensor
        jsonObject.put("result", sensors.size());

        // Tạo đối tượng JsonArray để lưu trữ danh sách sensors
        JSONArray jsonArray = new JSONArray();

        // Duyệt qua danh sách sensors
        for (Sensor sensor : sensors) {
            // Tạo đối tượng JSON con cho từng đối tượng Sensor
            JSONObject sensorObject = new JSONObject();
            sensorObject.put("id", sensor.getId());
            sensorObject.put("value", sensor.getValue());

            // Thêm đối tượng Sensor vào JsonArray
            jsonArray.put(sensorObject);
        }

        // Thiết lập thuộc tính "data" trong JsonObject là JsonArray đã tạo
        jsonObject.put("data", jsonArray);

        // Chuyển đối tượng JsonObject thành chuỗi JSON
        String jsonString = jsonObject.toString();

        // Trả về chuỗi JSON làm nội dung của phản hồi
        exchange.respond(CoAP.ResponseCode.CONTENT, jsonString, MediaTypeRegistry.APPLICATION_JSON);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        // Nhận payload từ yêu cầu CoAP
        byte[] payload = exchange.getRequestPayload();

        // Chuyển đổi chuỗi payload thành đối tượng JSONObject
        String payloadStr = new String(payload);
//        System.out.println(payloadStr);

        JSONObject jsonObject = new JSONObject(payloadStr);

        // Lấy giá trị từ đối tượng JSONObject
        String id = jsonObject.getString("id");
        Double value = jsonObject.getDouble("value");

//        System.out.println("id: " + id);
//        System.out.println("value: " + value);

        if(!checkSensorExistsById(sensors, id)){
            Sensor newSensor = new Sensor(id, value);
            sensors.add(newSensor);
        }else{
            updateSensorById(sensors, id, value);
        }

        changed();

        // Gửi phản hồi về cho yêu cầu CoAP
        Response response = new Response(CoAP.ResponseCode.CHANGED);
        response.setPayload("Đã nhận và xử lý payload thành công".getBytes());
        exchange.respond(response);
    }
}
