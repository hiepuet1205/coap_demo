package com.example.coap_demo.server.controller;

import com.example.coap_demo.model.AddSensorData;
import com.example.coap_demo.model.Sensor;
import com.example.coap_demo.server.resource.PerformanceResource;
import com.example.coap_demo.server.resource.SensorResource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class SensorController {
    private final SensorResource sensorResource;

    private final PerformanceResource performanceResource;

    private static final String COAP_SERVER_URL = "coap://localhost:5683/sensor";
    private static final String ADD_SENSOR_URL = "coap://localhost:5683/addSensor";
    private static final String COAP_PERFORMANCE_URL = "coap://localhost:5683/performance";

    public SensorController(SensorResource sensorResource, PerformanceResource performanceResource) {
        this.sensorResource = sensorResource;
        this.performanceResource = performanceResource;
    }

    @PutMapping(value = "/addSensor")
    @CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.PUT})
    public String addSensor(@RequestBody AddSensorData addSensorData){
        Long number = addSensorData.getNumber();

        CoapClient client = new CoapClient(ADD_SENSOR_URL);

        Request request = new Request(CoAP.Code.PUT);

        request.setConfirmable(true);

        JSONObject jsonObject = new JSONObject();

        // Thiết lập thuộc tính
        jsonObject.put("number", number);

        request.setPayload(jsonObject.toString().getBytes());

        CoapResponse coapResp = null;
        try {
            coapResp = client.advanced(request);
        } catch (ConnectorException | IOException e) {
            e.printStackTrace();
        }

        return "add sensor succesfull!";
    }

    @PutMapping(value = "/toggleSensor")
    @CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.PUT})
    public String toggleSensor(@RequestBody Sensor sensor){
        CoapClient client = new CoapClient(COAP_SERVER_URL + "/" + sensor.getId());

        System.out.println(sensor);

        Request request = new Request(CoAP.Code.PUT);

        request.setConfirmable(true);

        request.setPayload(sensor.sensorToJsonObject().toString().getBytes());

        CoapResponse coapResp = null;
        try {
            coapResp = client.advanced(request);
        } catch (ConnectorException | IOException e) {
            e.printStackTrace();
        }

        return "toggle sensor succesfull!";
    }

    @GetMapping(value = "/observe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET})
    public Flux<ServerSentEvent<String>> observeSensors() {
        // Tạo Flux<ServerSentEvent>
        return Flux.create(emitter -> {
            CoapClient client = new CoapClient(COAP_SERVER_URL);

            Request request = Request.newGet().setURI(COAP_SERVER_URL).setObserve();
            request.setConfirmable(true);

            CoapObserveRelation relation = client.observe(request, new CoapHandler() {

                @Override
                public void onLoad(CoapResponse coapResponse) {
                    // Lấy dữ liệu sensors từ SensorResource
                    String jsonString = sensorResource.sensorsToJsonObject().toString();

                    // Tạo đối tượng ServerSentEvent
                    ServerSentEvent<String> event = ServerSentEvent.builder(jsonString)
                            .id(Long.toString(System.currentTimeMillis()))
                            .build();

                    // Trả về ServerSentEvent
                    emitter.next(event);
                }

                @Override
                public void onError() {

                }
            });
        });
    }

    @GetMapping(value = "/performance", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET})
    public Flux<ServerSentEvent<String>> observePerformance() {
        return Flux.create(emitter -> {
            CoapClient client = new CoapClient(COAP_PERFORMANCE_URL);

            Request request = Request.newGet().setURI(COAP_PERFORMANCE_URL).setObserve();
            request.setConfirmable(true);

            CoapObserveRelation relation = client.observe(request, new CoapHandler() {

                @Override
                public void onLoad(CoapResponse coapResponse) {
                    // Lấy dữ liệu sensors từ SensorResource
                    String jsonString = performanceResource.performanceToJsonObject().toString();

                    // Tạo đối tượng ServerSentEvent
                    ServerSentEvent<String> event = ServerSentEvent.builder(jsonString)
                            .id(Long.toString(System.currentTimeMillis()))
                            .build();

                    // Trả về ServerSentEvent
                    emitter.next(event);
                }

                @Override
                public void onError() {

                }
            });
        });
    }
}
