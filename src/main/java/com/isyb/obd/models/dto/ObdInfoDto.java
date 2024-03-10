package com.isyb.obd.models.dto;

import lombok.Data;

@Data
public class ObdInfoDto {
    private String timestamp;
    private String latitude;
    private String longitude;
    private String voltage;
    private String temperature;
}
