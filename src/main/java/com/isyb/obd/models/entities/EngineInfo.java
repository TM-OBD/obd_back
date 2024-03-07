package com.isyb.obd.models.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Table(name = "engine_info")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EngineInfo {
    @Id
    private long id;
    private Timestamp timestamp;
    private double latitude;
    private double longitude;
    private double voltage;
    private double temperature;
}
