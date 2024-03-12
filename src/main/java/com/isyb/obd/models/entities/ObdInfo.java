package com.isyb.obd.models.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Table(name = "obd_info")
@Data
public class ObdInfo {
    @Id
    private Long id;
    private String device_id;
    private Timestamp timestamp;
    //    OBD
    private Timestamp utc_date;
    private Timestamp utc_time;
    private Float latitude;
    private Float longitude;
    private Float altitude;
    private Float speed;
    private Float course;
    private Integer satellites;
    private Float accelerometer_x;
    private Float accelerometer_y;
    private Float accelerometer_z;
    private Float gyroscope_x;
    private Float gyroscope_y;
    private Float gyroscope_z;
    private Float magnitude_x;
    private Float magnitude_y;
    private Float magnitude_z;
    private Float mems_temperature;
    private Float battery_voltage;
    private Float orientation_yaw;
    private Float orientation_pitch;
    private Float orientation_roll;
    private Float cellular_signal_level;
    private Float cpu_temperature;
    private Float cpu_hall_sensor_data;
    //    OBD 2
    private Float engine_load;
    private Float coolant_temperature;
    private Float fuel_pressure;
    private Float intake_manifold_pressure;
    private Integer engine_rpm;
    private Integer vehicle_speed;
    private Float timing_advance;
    private Float intake_air_temperature;
    private Float maf_air_flow_rate;
    private Float throttle_position;
    private Integer run_time_since_engine_start;
    private Integer distance_traveled_with_malfunction_indicator_lamp;
    private Float fuel_level_input;
    private Integer distance_traveled_since_codes_cleared;
    private Float barometric_pressure;
    private Float control_module_voltage;
    private Float absolute_load_value;
    private Float hybrid_battery_pack_remaining_life;
    private Float engine_oil_temperature;
    private Float engine_fuel_rate;
}

