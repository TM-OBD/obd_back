package com.isyb.obd.heartbeat;

import com.isyb.obd.models.dto.HeartbeatMonitorDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class HeartbeatScheduledTask {
    private static final Logger log = LogManager.getLogger(HeartbeatScheduledTask.class);

    @Autowired
    private HttpClient httpClient;

    //    TODO: подумать как сделать гибче или оставить так
    @Scheduled(initialDelay = 10000, fixedDelay = 2500)
    public void heartbeat() {
        String heartbeatServiceURL = "http://localhost:8081/webhook/heartbeat";

        HeartbeatMonitorDto heartbeatMonitorDto = new HeartbeatMonitorDto();
        heartbeatMonitorDto.setServiceName("obd backend");
        heartbeatMonitorDto.setStrategyProcessing("JVM");

        String monitorDtoJson = heartbeatMonitorDto.toJson();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(heartbeatServiceURL))
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(monitorDtoJson))
                .build();



        CompletableFuture<HttpResponse<String>> httpResponseCompletableFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        try {
            HttpResponse<String> stringHttpResponse = httpResponseCompletableFuture.get();

            log.info("Response status from heartbeat service: {}, POST: {}", stringHttpResponse.statusCode(), monitorDtoJson);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ConnectException) {
                log.error("Error sending heartbeat request: no connection with heartbeat service: {}, POST: {}", heartbeatServiceURL, monitorDtoJson);
            } else {
                e.printStackTrace();
            }
        }
    }
}