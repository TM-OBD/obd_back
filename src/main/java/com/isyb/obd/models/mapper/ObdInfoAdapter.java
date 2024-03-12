package com.isyb.obd.models.mapper;

import com.isyb.obd.models.dto.ObdInfoDto;
import com.isyb.obd.models.entities.ObdInfo;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class ObdInfoAdapter implements Adapter<ObdInfo, ObdInfoDto> {
    @Override
    public ObdInfo toEntity(ObdInfoDto obdInfoDto) {
        ObdInfo obdInfo = new ObdInfo();

        obdInfo.setTimestamp(obdInfoDto.getTimestamp() != null ? new Timestamp(Long.parseLong(obdInfoDto.getTimestamp())) : null);

//        OBD
        obdInfo.setUtc_date(obdInfoDto.getUtc_date() != null ? new Timestamp(Long.parseLong(obdInfoDto.getUtc_date())) : null);
        obdInfo.setUtc_time(obdInfoDto.getUtc_time() != null ? new Timestamp(Long.parseLong(obdInfoDto.getUtc_time())) : null);
        obdInfo.setLatitude(obdInfoDto.getLatitude() != null ? Float.valueOf(obdInfoDto.getLatitude()) : null);
        obdInfo.setLongitude(obdInfoDto.getLongitude() != null ? Float.valueOf(obdInfoDto.getLongitude()) : null);
        obdInfo.setAltitude(obdInfoDto.getAltitude() != null ? Float.valueOf(obdInfoDto.getAltitude()) : null);
        obdInfo.setSpeed(obdInfoDto.getSpeed() != null ? Float.valueOf(obdInfoDto.getSpeed()) : null);
        obdInfo.setCourse(obdInfoDto.getCourse() != null ? Float.valueOf(obdInfoDto.getCourse()) : null);
        obdInfo.setSatellites(obdInfoDto.getSatellites() != null ? Integer.valueOf(obdInfoDto.getSatellites()) : null);
        obdInfo.setAccelerometer_x(obdInfoDto.getAccelerometer_x() != null ? Float.valueOf(obdInfoDto.getAccelerometer_x()) : null);
        obdInfo.setAccelerometer_y(obdInfoDto.getAccelerometer_y() != null ? Float.valueOf(obdInfoDto.getAccelerometer_y()) : null);
        obdInfo.setAccelerometer_z(obdInfoDto.getAccelerometer_z() != null ? Float.valueOf(obdInfoDto.getAccelerometer_z()) : null);
        obdInfo.setGyroscope_x(obdInfoDto.getGyroscope_x() != null ? Float.valueOf(obdInfoDto.getGyroscope_x()) : null);
        obdInfo.setGyroscope_y(obdInfoDto.getGyroscope_y() != null ? Float.valueOf(obdInfoDto.getGyroscope_y()) : null);
        obdInfo.setGyroscope_z(obdInfoDto.getGyroscope_z() != null ? Float.valueOf(obdInfoDto.getGyroscope_z()) : null);
        obdInfo.setMagnitude_x(obdInfoDto.getMagnitude_x() != null ? Float.valueOf(obdInfoDto.getMagnitude_x()) : null);
        obdInfo.setMagnitude_y(obdInfoDto.getMagnitude_y() != null ? Float.valueOf(obdInfoDto.getMagnitude_y()) : null);
        obdInfo.setMagnitude_z(obdInfoDto.getMagnitude_z() != null ? Float.valueOf(obdInfoDto.getMagnitude_z()) : null);
        obdInfo.setMems_temperature(obdInfoDto.getMems_temperature() != null ? Float.valueOf(obdInfoDto.getMems_temperature()) : null);
        obdInfo.setBattery_voltage(obdInfoDto.getBattery_voltage() != null ? Float.valueOf(obdInfoDto.getBattery_voltage()) : null);
        obdInfo.setOrientation_yaw(obdInfoDto.getOrientation_yaw() != null ? Float.valueOf(obdInfoDto.getOrientation_yaw()) : null);
        obdInfo.setOrientation_pitch(obdInfoDto.getOrientation_pitch() != null ? Float.valueOf(obdInfoDto.getOrientation_pitch()) : null);
        obdInfo.setOrientation_roll(obdInfoDto.getOrientation_roll() != null ? Float.valueOf(obdInfoDto.getOrientation_roll()) : null);
        obdInfo.setCellular_signal_level(obdInfoDto.getCellular_signal_level() != null ? Float.valueOf(obdInfoDto.getCellular_signal_level()) : null);
        obdInfo.setCpu_temperature(obdInfoDto.getCpu_temperature() != null ? Float.valueOf(obdInfoDto.getCpu_temperature()) : null);
        obdInfo.setCpu_hall_sensor_data(obdInfoDto.getCpu_hall_sensor_data() != null ? Float.valueOf(obdInfoDto.getCpu_hall_sensor_data()) : null);

//        OBD 2
        obdInfo.setEngine_load(obdInfoDto.getEngine_load() != null ? Float.valueOf(obdInfoDto.getEngine_load()) : null);
        obdInfo.setCoolant_temperature(obdInfoDto.getCoolant_temperature() != null ? Float.valueOf(obdInfoDto.getCoolant_temperature()) : null);
        obdInfo.setFuel_pressure(obdInfoDto.getFuel_pressure() != null ? Float.valueOf(obdInfoDto.getFuel_pressure()) : null);
        obdInfo.setIntake_manifold_pressure(obdInfoDto.getIntake_manifold_pressure() != null ? Float.valueOf(obdInfoDto.getIntake_manifold_pressure()) : null);
        obdInfo.setEngine_rpm(obdInfoDto.getEngine_rpm() != null ? Integer.valueOf(obdInfoDto.getEngine_rpm()) : null);
        obdInfo.setVehicle_speed(obdInfoDto.getVehicle_speed() != null ? Integer.valueOf(obdInfoDto.getVehicle_speed()) : null);
        obdInfo.setTiming_advance(obdInfoDto.getTiming_advance() != null ? Float.valueOf(obdInfoDto.getTiming_advance()) : null);
        obdInfo.setIntake_air_temperature(obdInfoDto.getIntake_air_temperature() != null ? Float.valueOf(obdInfoDto.getIntake_air_temperature()) : null);
        obdInfo.setMaf_air_flow_rate(obdInfoDto.getMaf_air_flow_rate() != null ? Float.valueOf(obdInfoDto.getMaf_air_flow_rate()) : null);
        obdInfo.setThrottle_position(obdInfoDto.getThrottle_position() != null ? Float.valueOf(obdInfoDto.getThrottle_position()) : null);
        obdInfo.setRun_time_since_engine_start(obdInfoDto.getRun_time_since_engine_start() != null ? Integer.valueOf(obdInfoDto.getRun_time_since_engine_start()) : null);
        obdInfo.setDistance_traveled_with_malfunction_indicator_lamp(obdInfoDto.getDistance_traveled_with_malfunction_indicator_lamp() != null ? Integer.valueOf(obdInfoDto.getDistance_traveled_with_malfunction_indicator_lamp()) : null);
        obdInfo.setFuel_level_input(obdInfoDto.getFuel_level_input() != null ? Float.valueOf(obdInfoDto.getFuel_level_input()) : null);
        obdInfo.setDistance_traveled_since_codes_cleared(obdInfoDto.getDistance_traveled_since_codes_cleared() != null ? Integer.valueOf(obdInfoDto.getDistance_traveled_since_codes_cleared()) : null);
        obdInfo.setBarometric_pressure(obdInfoDto.getBarometric_pressure() != null ? Float.valueOf(obdInfoDto.getBarometric_pressure()) : null);
        obdInfo.setControl_module_voltage(obdInfoDto.getControl_module_voltage() != null ? Float.valueOf(obdInfoDto.getControl_module_voltage()) : null);
        obdInfo.setAbsolute_load_value(obdInfoDto.getAbsolute_load_value() != null ? Float.valueOf(obdInfoDto.getAbsolute_load_value()) : null);
        obdInfo.setHybrid_battery_pack_remaining_life(obdInfoDto.getHybrid_battery_pack_remaining_life() != null ? Float.valueOf(obdInfoDto.getHybrid_battery_pack_remaining_life()) : null);
        obdInfo.setEngine_oil_temperature(obdInfoDto.getEngine_oil_temperature() != null ? Float.valueOf(obdInfoDto.getEngine_oil_temperature()) : null);
        obdInfo.setEngine_fuel_rate(obdInfoDto.getEngine_fuel_rate() != null ? Float.valueOf(obdInfoDto.getEngine_fuel_rate()) : null);


        return obdInfo;
    }

    @Override
    public ObdInfoDto toDto(ObdInfo obdInfo) {
        return null;
    }
}
