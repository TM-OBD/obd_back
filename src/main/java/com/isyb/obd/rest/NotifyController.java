package com.isyb.obd.rest;


import com.isyb.obd.initialization_components.DatabaseInit;
import com.isyb.obd.models.dto.NotifyResultDto;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.isyb.obd.util.Sources.NOTIFY;

@RestController
@RequiredArgsConstructor
public class NotifyController {

    private static final Logger log = LogManager.getLogger(DatabaseInit.class);

    @GetMapping(NOTIFY)
    public Mono<? extends NotifyResultDto> notifyHandle(
            @PathVariable String deviceId,
            @RequestParam("EV") String eventId,
            @RequestParam("TS") String timestamp,
            @RequestParam("VIN") String vehicleVIN
    ) {

        log.info("Received notify. deviceId: {}, EV: {}, TS {}, VIN {}", deviceId, eventId, timestamp, vehicleVIN);

        NotifyResultDto done = new NotifyResultDto.Done(vehicleVIN);
        NotifyResultDto.Failed invalidVin = new NotifyResultDto.Failed("Invalid VIN");

        return Mono.just(done);
    }
}
