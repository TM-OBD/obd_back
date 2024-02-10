package com.isyb.obd.models.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class EngineInfoDto {
    private String timestamp;
    private String latitude;
    private String longitude;
    private String voltage;
    private String temperature;
}
