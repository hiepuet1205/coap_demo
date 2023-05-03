package com.example.coap_demo.server.controller;

import com.example.coap_demo.server.resource.SensorResource;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.Request;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class SensorController {
    private final SensorResource sensorResource;

    private static final String COAP_SERVER_URL = "coap://localhost:5683/sensor"; // Địa chỉ URI của server CoAP

    public SensorController(SensorResource sensorResource) {
        this.sensorResource = sensorResource;
    }

    @GetMapping(value = "/observe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
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
}
