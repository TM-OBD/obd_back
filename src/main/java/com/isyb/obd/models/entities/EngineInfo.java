package com.isyb.obd.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "engine_info")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EngineInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Timestamp timestamp;
    private double latitude;
    private double longitude;
    private double voltage;
    private double temperature;
}
