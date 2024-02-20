package com.isyb.obd.rest;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.isyb.obd.util.Sources.HEALTH_V1;

@RestController
@RequiredArgsConstructor
public class HeartbeatControllerV1 {
    private static final Logger log = LogManager.getLogger(HeartbeatControllerV1.class);

    @GetMapping(HEALTH_V1)
    public ResponseEntity<Void> health() {
        log.info("Health received");
        return ResponseEntity.ok().build();
    }
}
