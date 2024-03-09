package com.isyb.obd.rest;


import com.isyb.obd.models.dto.NotifyDto;
import com.isyb.obd.models.dto.NotifyResponseDto;
import com.isyb.obd.models.entities.FunctionResultOfLoginLogoutNotify;
import com.isyb.obd.models.repos.NotifyRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.sql.Timestamp;

import static com.isyb.obd.models.dto.NotifyDto.NotifyStatus.*;
import static com.isyb.obd.util.Sources.NOTIFY;

@RestController
@RequiredArgsConstructor
public class NotifyController {

    private static final Logger log = LogManager.getLogger(NotifyController.class);

    @Autowired
    private Validator validator;

    @Autowired
    private NotifyRepository notifyRepository;

    @GetMapping(NOTIFY)
    public Mono<ResponseEntity<? extends NotifyResponseDto>> notifyHandle(
            @PathVariable String deviceId,
            @RequestParam("EV") String eventId,
            @RequestParam("TS") String timestamp,
            @RequestParam(value = "VIN", required = false) String vehicleVIN
    ) {

        return Mono
                .fromCallable(() -> {
                    NotifyDto notifyDto = new NotifyDto();
                    log.info("[deviceId: {}, EV: {}, TS: {}, VIN: {}; NotifyStatus: {}]", deviceId, eventId, timestamp, vehicleVIN, notifyDto.getNotifyStatus().toString());

                    return notifyDto;
                })
                .map(dto -> {
                    if (dto.getNotifyStatus().equals(CREATED)) {

                        try {
                            Long.parseLong(timestamp);

                            dto.setNotifyStatus(PARSER_SUCCESSFULLY);

                            log.info("[deviceId: {}, EV: {}, TS: {}, VIN: {}; NotifyStatus: {}] TS has been transformed into long", deviceId, eventId, timestamp, vehicleVIN, dto.getNotifyStatus().toString());
                        } catch (NumberFormatException numberFormatException) {
                            dto.setNotifyStatus(PARSER_FAILS("Invalid timestamp"));

                            log.warn("[deviceId: {}, EV: {}, TS: {}, VIN: {}; NotifyStatus: {}] TS HAS NOT transformed into long", deviceId, eventId, timestamp, vehicleVIN, dto.getNotifyStatus().toString());
                        }
                    }

                    return dto;
                })
                .map(dto -> {

                    if (dto.getNotifyStatus().equals(PARSER_SUCCESSFULLY)) {

                        dto.setDeviceId(deviceId);
                        dto.setEvent(eventId);
                        dto.setTimestamp(new Timestamp(Long.valueOf(timestamp)));
                        dto.setVehicleVIN(vehicleVIN);


                        Errors errors = validator.validateObject(dto);

                        if (errors.hasErrors()) {
                            dto.setNotifyStatus(VALIDATE_FAILS(errors.toString()));
                            log.warn("[deviceId: {}, EV: {}, TS: {}, VIN: {}; NotifyStatus: {}] Obj HAS NOT been transformed, NotifyDto: {}", deviceId, eventId, timestamp, vehicleVIN, dto.getNotifyStatus().toString(), dto.toString());
                            return dto;
                        }

                        dto.setNotifyStatus(VALIDATE_SUCCESSFULLY);
                        log.info("[deviceId: {}, EV: {}, TS: {}, VIN: {}; NotifyStatus: {}] Obj has been transformed", deviceId, eventId, timestamp, vehicleVIN, dto.getNotifyStatus().toString());
                        return dto;
                    }

                    return dto;
                })
                .publishOn(Schedulers.boundedElastic())
                .map(dto -> {
                    if (dto.getNotifyStatus().equals(VALIDATE_SUCCESSFULLY)) {

                        dto.setNotifyStatus(SAVE_PROGRESSING);

                        switch (dto.getEvent()) {
                            case "1" -> {
                                Mono<FunctionResultOfLoginLogoutNotify> functionOnLoginDeviceResultMono = notifyRepository.onLoginDevice(dto.getDeviceId());
//                                functionOnLoginDeviceResultMono.subscribe(result -> log.info("[deviceId: {}, EV: {}, TS: {}, VIN: {}; NotifyStatus: {}] (async calling r2dbc function) Saved result: {}, saved result message: {}, progress id: {}", deviceId, eventId, timestamp, vehicleVIN, dto.getNotifyStatus().toString(), result.getSavedresult(), result.getSavedresultmessage(), result.getProgressid()));
                                FunctionResultOfLoginLogoutNotify block = functionOnLoginDeviceResultMono.block();
                                log.info("[deviceId: {}, EV: {}, TS: {}, VIN: {}; NotifyStatus: {}] (blocking call r2dbc function) Saved result: {}, saved result message: {}, progress id: {}", deviceId, eventId, timestamp, vehicleVIN, dto.getNotifyStatus().toString(), block.getSavedresult(), block.getSavedresultmessage(), block.getProgressid());
                            }
                            case "0" -> {
                                Mono<FunctionResultOfLoginLogoutNotify> functionOnLoginDeviceResultMono = notifyRepository.onLogoutDevice(dto.getDeviceId());
//                                functionOnLoginDeviceResultMono.subscribe(result -> log.info("[deviceId: {}, EV: {}, TS: {}, VIN: {}; NotifyStatus: {}] (async calling r2dbc function) Saved result: {}, saved result message: {}, progress id: {}", deviceId, eventId, timestamp, vehicleVIN, dto.getNotifyStatus().toString(), result.getSavedresult(), result.getSavedresultmessage(), result.getProgressid()));
                                FunctionResultOfLoginLogoutNotify block = functionOnLoginDeviceResultMono.block();
                                log.info("[deviceId: {}, EV: {}, TS: {}, VIN: {}; NotifyStatus: {}] (blocking call r2dbc function) Saved result: {}, saved result message: {}, progress id: {}", deviceId, eventId, timestamp, vehicleVIN, dto.getNotifyStatus().toString(), block.getSavedresult(), block.getSavedresultmessage(), block.getProgressid());
                            }
                        }

                        return dto;
                    }

                    return dto;
                })
                .flatMap(dto -> {
                    if (dto.getNotifyStatus().equals(SAVE_PROGRESSING)) {

                        return notifyRepository.pollDeviceInProgress(deviceId)
                                .map(result -> {

//                                    поскольку по умолчанию уровень изоляции транзакций READ COMMITED, то не исключаем вероятность того, что данные не будут изменены или удалены другими транзакциями до конца текущей транзакции
//                                    при необходимости поставлю уровень SERIALIZABLE и проверю, будет ли ещё эта ошибка падать. пока это не критично
                                    try {
                                        dto.setNotifyStatus(NotifyDto.NotifyStatus.valueOf(result.getSavedresult()));
                                    } catch (NullPointerException e) {
                                        log.error("[deviceId: {}, EV: {}, TS: {}, VIN: {}; NotifyStatus: {}] {}", deviceId, eventId, timestamp, vehicleVIN, dto.getNotifyStatus().toString(), result.toString());
                                    }

                                    if (dto.getNotifyStatus().equals(SAVED_SUCCESSFULLY)) {
                                        NotifyResponseDto.Done done = new NotifyResponseDto.Done(deviceId);

                                        log.info("[deviceId: {}, EV: {}, TS: {}, VIN: {}; NotifyStatus: {}] Response has been sent. NotifyResponseDto: {}", deviceId, eventId, timestamp, vehicleVIN, dto.getNotifyStatus().toString(), done.toString());

                                        return ResponseEntity.ok().body(done);
                                    } else if (dto.getNotifyStatus().equals(SAVED_FAILS)) {
                                        dto.setNotifyStatus(SAVED_FAILS(result.getSavedresultmessage()));

                                        NotifyResponseDto.Failed failed = new NotifyResponseDto.Failed(dto.getNotifyStatus().getErrorMessage());

                                        log.warn("[deviceId: {}, EV: {}, TS: {}, VIN: {}; NotifyStatus: {}] Response has been sent. NotifyResponseDto: {}", deviceId, eventId, timestamp, vehicleVIN, dto.getNotifyStatus().toString(), failed.toString());

                                        return ResponseEntity.badRequest().body(failed);
                                    }

                                    NotifyResponseDto.Failed failed = new NotifyResponseDto.Failed(dto.getNotifyStatus().getErrorMessage());

                                    log.warn("[deviceId: {}, EV: {}, TS: {}, VIN: {}; NotifyStatus: {}] Response has been sent. NotifyResponseDto: {}", deviceId, eventId, timestamp, vehicleVIN, dto.getNotifyStatus().toString(), failed.toString());

                                    return ResponseEntity.badRequest().body(failed);
                                });
                    }
                    NotifyResponseDto.Failed failed = new NotifyResponseDto.Failed(dto.getNotifyStatus().getErrorMessage());

                    log.warn("[deviceId: {}, EV: {}, TS: {}, VIN: {}; NotifyStatus: {}] Response has been sent. NotifyResponseDto: {}", deviceId, eventId, timestamp, vehicleVIN, dto.getNotifyStatus().toString(), failed.toString());

                    return Mono.fromCallable(() -> ResponseEntity.badRequest().body(failed));
                });
    }
}
