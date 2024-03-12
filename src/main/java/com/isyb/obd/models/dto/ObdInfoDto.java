package com.isyb.obd.models.dto;

import lombok.Data;

@Data
public class ObdInfoDto {
    private String timestamp;
    //    OBD 
    private String utc_date;
    private String utc_time;
    private String latitude;
    private String longitude;
    private String altitude;
    private String speed;
    private String course;
    private String satellites;
    private String accelerometer_x;
    private String accelerometer_y;
    private String accelerometer_z;
    private String gyroscope_x;
    private String gyroscope_y;
    private String gyroscope_z;
    private String magnitude_x;
    private String magnitude_y;
    private String magnitude_z;
    private String mems_temperature;
    private String battery_voltage;
    private String orientation_yaw;
    private String orientation_pitch;
    private String orientation_roll;
    private String cellular_signal_level;
    private String cpu_temperature;
    private String cpu_hall_sensor_data;
    //    OBD 2
    private String engine_load;
    private String coolant_temperature;
    private String fuel_pressure;
    private String intake_manifold_pressure;
    private String engine_rpm;
    private String vehicle_speed;
    private String timing_advance;
    private String intake_air_temperature;
    private String maf_air_flow_rate;
    private String throttle_position;
    private String run_time_since_engine_start;
    private String distance_traveled_with_malfunction_indicator_lamp;
    private String fuel_level_input;
    private String distance_traveled_since_codes_cleared;
    private String barometric_pressure;
    private String control_module_voltage;
    private String absolute_load_value;
    private String hybrid_battery_pack_remaining_life;
    private String engine_oil_temperature;
    private String engine_fuel_rate;
}
