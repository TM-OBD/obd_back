package com.isyb.obd.models.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Table(name = "devices")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Device {
    @Id
    private Long id;
    private String deviceId;
    private Byte flag;
    private Timestamp createdAt;
    private Timestamp updatedFlag;
    private String VIN;
}
