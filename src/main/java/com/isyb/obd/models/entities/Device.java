package com.isyb.obd.models.entities;


import java.sql.Timestamp;

public class Device {
    private Long id;
    private String deviceId;
    private Byte flag;
    private Timestamp createdAt;
    private Timestamp updatedFlag;
    private String VIN;
}
