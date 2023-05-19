package com.example.coap_demo.server.resource;

import com.example.coap_demo.model.Sensor;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

public class PerformanceResource extends CoapResource {
    private static Integer DELAY = 0;
    private static double CPU = 0;
    private static double RAM = 0;

    public PerformanceResource(String name) {
        super(name);

        setObservable(true); // enable observing
        setObserveType(CoAP.Type.CON); // configure the notification type to CONs

        getAttributes().setTitle("Performance Observable Resource");
        getAttributes().setObservable(); // mark observable in the Link-Format
    }

    @Override
    public void handleGET(CoapExchange exchange) {

        JSONObject jsonObject = this.performanceToJsonObject();

        // Trả về chuỗi JSON làm nội dung của phản hồi
        exchange.respond(CoAP.ResponseCode.CONTENT, jsonObject.toString(), MediaTypeRegistry.APPLICATION_JSON);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        // Nhận payload từ yêu cầu CoAP
        byte[] payload = exchange.getRequestPayload();

        // Chuyển đổi chuỗi payload thành đối tượng JSONObject
        String payloadStr = new String(payload);

        JSONObject jsonObject = new JSONObject(payloadStr);

        // Lấy giá trị từ đối tượng JSONObject
        String dateStr = jsonObject.getString("timeStart");
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        Date timeStart;
        try {
            timeStart = sdf.parse(dateStr);
            System.out.println(timeStart);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // cap nhat number sensor currently
        DELAY = Math.toIntExact(new Date(System.currentTimeMillis()).getTime() - timeStart.getTime());

//        try {
//            CPU = extractCPU();
//            RAM = extractRAM();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        changed();

        Response response = new Response(CoAP.ResponseCode.CHANGED);
        response.setPayload("Đã nhận và xử lý payload thành công".getBytes());
        exchange.respond(response);
    }

    public JSONObject performanceToJsonObject() {
        // Tạo đối tượng JSON
        JSONObject jsonObject = new JSONObject();

        // Thiết lập thuộc tính "success" với giá trị true
        jsonObject.put("success", true);

        jsonObject.put("delay", DELAY);

        jsonObject.put("cpu", CPU);

        jsonObject.put("ram", RAM);

        return jsonObject;
    }

    public double extractCPU() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("top", "-b", "-n", "1");
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        double cpuUsage = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Cpu(s)")) {
                // Xử lý thông tin CPU
                String[] cpuInfo = line.split(",");
                for (String info : cpuInfo) {
                    if (info.contains("id")) {
                        cpuUsage = 100 - Double.parseDouble(info.replaceAll("[^\\d.]", ""));
                        System.out.println("Phần trăm CPU đang sử dụng: " + cpuUsage + "%");
                    }
                }
            }
        }
        return cpuUsage;
    }

    public double extractRAM() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("top", "-b", "-n", "1");
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        double ramUsage = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("KiB Mem")) {
                // Xử lý thông tin RAM
                String[] ramInfo = line.split(",");
                for (String info : ramInfo) {
                    if (info.contains("used")) {
                        ramUsage = Double.parseDouble(info.replaceAll("[^\\d.]", ""));
                        System.out.println("Phần trăm RAM đang sử dụng: " + ramUsage + " KiB");
                    }
                }
            }
        }
        return ramUsage;
    }
}
