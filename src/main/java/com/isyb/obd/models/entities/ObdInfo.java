package com.isyb.obd.models.entities;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Table(name = "obd_info")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ObdInfo {
    @Id
    private long id;
    private String device_id;
    private Timestamp timestamp;
    private double latitude;
    private double longitude;
    private double voltage;
    private double temperature;
}
