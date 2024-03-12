package com.isyb.obd.rest;

import com.isyb.obd.models.dto.ObdInfoDto;
import com.isyb.obd.models.dto.ObdInfoFlowDto;
import com.isyb.obd.models.dto.ResponseForObdDto;
import com.isyb.obd.models.entities.ObdInfo;
import com.isyb.obd.models.mapper.ObdInfoAdapter;
import com.isyb.obd.models.repos.ObdInfoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.isyb.obd.cache.ObdInfoFieldCache._OBD_INFO_FIELDS_CACHE;
import static com.isyb.obd.models.dto.ObdInfoFlowDto.ObdInfoStatus.*;
import static com.isyb.obd.util.Sources.INFO_V1;

@RestController
@RequiredArgsConstructor
public class ObdInfoController {
    private static final Logger log = LogManager.getLogger(ObdInfoController.class);
    @Autowired
    private ObdInfoRepository infoRepository;
    @Autowired
    private ObdInfoAdapter obdInfoAdapter;

    @PostMapping(INFO_V1)
    public Mono<ResponseEntity<? extends ResponseForObdDto>> handlePost(@PathVariable String deviceId, @RequestBody String payload) {
        return Mono
                .fromCallable(() -> {
                    log.info("[deviceId: {}]", deviceId);

                    return payload;
                })
                .map(input -> {

                    Map<String, String> tempMap = new HashMap<>();
                    String[] split = input.split(",");
                    for (String pair : split) {
                        String[] keyValue = pair.split(":");
                        if (keyValue.length == 2 && !tempMap.containsKey(keyValue[0])) {
                            tempMap.put(keyValue[0], keyValue[1]);
                        }
                    }

                    Map<String, String> output = new HashMap<>();

                    for (Map.Entry<String, String> entry : tempMap.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();

                        if (value.matches("-?\\d+;-?\\d+;-?\\d+")) {
                            String[] split1 = value.split(";");
                            output.put(key + "_x", split1[0]);
                            output.put(key + "_y", split1[1]);
                            output.put(key + "_z", split1[2]);
                        } else {
                            output.put(key, value);
                        }
                    }

                    log.info("[deviceId: {}; payload {}; ObdInfoStatus: -]: Payload has been transformed to Map<String, String> successfully: {}", deviceId, payload, output.toString());
                    return output;
                })
                .map(input -> {

                    ObdInfoDto obdInfoDto = new ObdInfoDto();
                    ObdInfoFlowDto obdInfoFlowDto = new ObdInfoFlowDto();

                    Class<? extends ObdInfoDto> aClass = obdInfoDto.getClass();

                    StringBuilder fieldsThatNotExists = new StringBuilder();

                    for (Map.Entry<String, String> entry : input.entrySet()) {
                        Optional<String> s = Optional.ofNullable(_OBD_INFO_FIELDS_CACHE.get(entry.getKey()));

                        if (s.isPresent()) {
                            Optional<Field> declaredField = Optional.empty();

                            try {
                                declaredField = Optional.of(aClass.getDeclaredField(s.get()));
                            } catch (NoSuchFieldException e) {
                                obdInfoFlowDto.setObdInfoStatus(FILLED_FAILS(e.getMessage()));

                                log.warn("[deviceId: {}; payload {}; ObdInfoStatus: {}] NoSuchFieldException: {}", deviceId, payload, obdInfoFlowDto.getObdInfoStatus(), e.getMessage());
                                return obdInfoFlowDto;
                            }

                            if (declaredField.isPresent()) {
                                Field field = declaredField.get();
                                field.setAccessible(true);

                                try {
                                    field.set(obdInfoDto, entry.getValue());
                                } catch (IllegalAccessException e) {
                                    obdInfoFlowDto.setObdInfoStatus(FILLED_FAILS(e.getMessage()));

                                    log.warn("[deviceId: {}; payload {}; ObdInfoStatus: {}] IllegalAccessException: {}", deviceId, payload, obdInfoFlowDto.getObdInfoStatus(), e.getMessage());
                                    return obdInfoFlowDto;
                                }
                            } else {
                                obdInfoFlowDto.setObdInfoStatus(FILLED_FAILS("Declared field does not exist"));

                                log.error("[deviceId: {}; payload {}; ObdInfoStatus: {}]: {}", deviceId, payload, obdInfoFlowDto.getObdInfoStatus(), obdInfoFlowDto.getObdInfoStatus().getErrorMessage());
                                return obdInfoFlowDto;
                            }

                        } else {
                            fieldsThatNotExists.append(entry.getKey() + " ");
                        }
                    }

                    if (!fieldsThatNotExists.isEmpty()) {
                        obdInfoFlowDto.setObdInfoStatus(FILLED_FAILS("This field does not exist: " + fieldsThatNotExists.toString()));

                        log.warn("[deviceId: {}; payload {}; ObdInfoStatus: {}]: {}", deviceId, payload, obdInfoFlowDto.getObdInfoStatus(), obdInfoFlowDto.getObdInfoStatus().getErrorMessage());
                        return obdInfoFlowDto;
                    }

                    obdInfoFlowDto.setObdInfoDto(obdInfoDto);
                    obdInfoFlowDto.setObdInfoStatus(FILLED_SUCCESSFULLY);

                    log.info("[deviceId: {}; payload {}; ObdInfoStatus: {}]: ObdInfoDto has been filled successfully: {}", deviceId, payload, obdInfoFlowDto.getObdInfoStatus(), obdInfoFlowDto.getObdInfoDto().toString());
                    return obdInfoFlowDto;
                })
                .map(obdFlow -> {

                    if (obdFlow.getObdInfoStatus().equals(FILLED_SUCCESSFULLY)) {
                        ObdInfoDto obdInfoDto = obdFlow.getObdInfoDto();

                        try {
                            ObdInfo entity = obdInfoAdapter.toEntity(obdInfoDto);
                            entity.setDevice_id(deviceId);
                            obdFlow.setObdInfo(entity);
                        } catch (NumberFormatException e) {
                            obdFlow.setObdInfoStatus(MAPPED_FAILS(e.getMessage()));

                            log.warn("[deviceId: {}; payload {}; ObdInfoStatus: {}] Mapped from dto to entity failed: {}", deviceId, payload, obdFlow.getObdInfoStatus(), e.getMessage());
                            return obdFlow;
                        }

                        obdFlow.setObdInfoStatus(MAPPED_SUCCESSFULLY);

                        log.info("[deviceId: {}; payload {}; ObdInfoStatus: {}] Mapped from dto to entity successfully: {}", deviceId, payload, obdFlow.getObdInfoStatus(), obdFlow.getObdInfo().toString());
                        return obdFlow;
                    }
                    return obdFlow;
                })
                .publishOn(Schedulers.boundedElastic())
                .map(obdFlow -> {

                    if (obdFlow.getObdInfoStatus().equals(MAPPED_SUCCESSFULLY)) {
                        ObdInfo obdInfo = obdFlow.getObdInfo();
                        Mono<ObdInfo> save = infoRepository.save(obdInfo);
                        save.subscribe(object -> log.info("[deviceId: {}; payload {}; ObdInfoStatus: {}] (async call r2dbc) Obd info has been saved: {}", deviceId, payload, obdFlow.getObdInfoStatus(), object.toString()));

                        obdFlow.setObdInfoStatus(SAVED);
                    }

                    return obdFlow;
                })
                .flatMap(obdFlow -> {

                    if (obdFlow.getObdInfoStatus().equals(SAVED)) {

                        ResponseForObdDto.Done done = new ResponseForObdDto.Done("1");

                        log.info("[deviceId: {}; payload {}; ObdInfoStatus: {}] Processing ended, return result: {}", deviceId, payload, obdFlow.getObdInfoStatus(), done.toString());
                        return Mono.fromCallable(() -> ResponseEntity.ok().body(done));
                    }

                    ResponseForObdDto.Failed failed = new ResponseForObdDto.Failed(obdFlow.getObdInfoStatus().getErrorMessage());

                    log.warn("[deviceId: {}; payload {}; ObdInfoStatus: {}] Processing ended, return result: {}", deviceId, payload, obdFlow.getObdInfoStatus(), failed.toString());
                    return Mono.fromCallable(() -> ResponseEntity.badRequest().body(failed));
                });
    }
}