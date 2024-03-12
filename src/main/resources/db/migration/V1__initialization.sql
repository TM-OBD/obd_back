CREATE TABLE obd_info
(
    id                                                BIGSERIAL    NOT NULL PRIMARY KEY,
    device_id                                         VARCHAR(255) NOT NULL,
    timestamp                                         TIMESTAMP(6),
--     OBD
    utc_date                                          TIMESTAMP(6),
    utc_time                                          TIMESTAMP(8),
    latitude                                          FLOAT,
    longitude                                         FLOAT,
    altitude                                          FLOAT,
    speed                                             FLOAT,
    course                                            FLOAT,
    satellites                                        INTEGER,
    accelerometer_x                                   FLOAT,
    accelerometer_y                                   FLOAT,
    accelerometer_z                                   FLOAT,
    gyroscope_x                                       FLOAT,
    gyroscope_y                                       FLOAT,
    gyroscope_z                                       FLOAT,
    magnitude_x                                       FLOAT,
    magnitude_y                                       FLOAT,
    magnitude_z                                       FLOAT,
    mems_temperature                                  FLOAT,
    battery_voltage                                   FLOAT,
    orientation_yaw                                   FLOAT,
    orientation_pitch                                 FLOAT,
    orientation_roll                                  FLOAT,
    cellular_signal_level                             FLOAT,
    cpu_temperature                                   FLOAT,
    cpu_hall_sensor_data                              FLOAT,
--     OBD II
    engine_load                                       FLOAT,
    coolant_temperature                               FLOAT,
    fuel_pressure                                     FLOAT,
    intake_manifold_pressure                          FLOAT,
    engine_rpm                                        INTEGER,
    vehicle_speed                                     INTEGER,
    timing_advance                                    FLOAT,
    intake_air_temperature                            FLOAT,
    maf_air_flow_rate                                 FLOAT,
    throttle_position                                 FLOAT,
    run_time_since_engine_start                       INTEGER,
    distance_traveled_with_malfunction_indicator_lamp INTEGER,
    fuel_level_input                                  FLOAT,
    distance_traveled_since_codes_cleared             INTEGER,
    barometric_pressure                               FLOAT,
    control_module_voltage                            FLOAT,
    absolute_load_value                               FLOAT,
    hybrid_battery_pack_remaining_life                FLOAT,
    engine_oil_temperature                            FLOAT,
    engine_fuel_rate                                  FLOAT
);
CREATE TABLE obd_info_fields
(
    id         BIGSERIAL    NOT NULL PRIMARY KEY,
    field_name VARCHAR(255) NOT NULL,
    pid        VARCHAR(255) NOT NULL
);

INSERT INTO obd_info_fields (field_name, pid)
VALUES ('timestamp', '0'),
--        OBD
       ('utc_date', '11'),
       ('utc_time', '10'),
       ('latitude', 'a'),
       ('longitude', 'b'),
       ('altitude', 'c'),
       ('speed', 'd'),
       ('course', 'e'),
       ('satellites', 'f'),
       ('accelerometer_x', '20_x'),
       ('accelerometer_y', '20_y'),
       ('accelerometer_z', '20_z'),
       ('gyroscope_x', '21_x'),
       ('gyroscope_y', '21_y'),
       ('gyroscope_z', '21_z'),
       ('magnitude_x', '22'),
       ('magnitude_y', '22'),
       ('magnitude_z', '22'),
       ('mems_temperature', '23'),
       ('battery_voltage', '24'),
       ('orientation_yaw', '25_x'),
       ('orientation_pitch', '25_y'),
       ('orientation_roll', '25_z'),
       ('cellular_signal_level', '81'),
       ('cpu_temperature', '82'),
       ('cpu_hall_sensor_data', '83'),
--        OBD II
       ('engine_load', '104'),
       ('coolant_temperature', '105'),
       ('fuel_pressure', '10a'),
       ('intake_manifold_pressure', '10b'),
       ('engine_rpm', '10c'),
       ('vehicle_speed', '10d'),
       ('timing_advance', '10e'),
       ('intake_air_temperature', '10f'),
       ('maf_air_flow_rate', '110'),
       ('throttle_position', '111'),
       ('run_time_since_engine_start', '11f'),
       ('distance_traveled_with_malfunction_indicator_lamp', '121'),
       ('fuel_level_input', '12f'),
       ('distance_traveled_since_codes_cleared', '131'),
       ('barometric_pressure', '133'),
       ('control_module_voltage', '142'),
       ('absolute_load_value', '143'),
       ('hybrid_battery_pack_remaining_life', '15b'),
       ('engine_oil_temperature', '15c'),
       ('engine_fuel_rate', '15e');

CREATE TABLE devices
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    deviceid    VARCHAR(255) NOT NULL UNIQUE,
    flag        BIT          NOT NULL,
    createdat   TIMESTAMP(6) NOT NULL,
    updatedflag TIMESTAMP(6) NOT NULL,
    vin         VARCHAR(255)
);

CREATE TABLE device_save_progressing
(
    id                 BIGSERIAL    NOT NULL PRIMARY KEY,
    deviceid           VARCHAR(255) NOT NULL,
    savedstatus        VARCHAR(255) NOT NULL,
    savedresultmessage VARCHAR(255) NOT NULL
);

CREATE OR REPLACE FUNCTION on_login_device(
    deviceid VARCHAR(255),
    OUT savedresult VARCHAR(255),
    OUT savedresultmessage VARCHAR(255),
    OUT progressid BIGINT
)
    RETURNS RECORD
    LANGUAGE plpgsql
AS
$$
DECLARE
    flagvalue BIT;
BEGIN
    IF (SELECT count(*)
        FROM device_save_progressing
        WHERE device_save_progressing.deviceid = on_login_device.deviceid) = 0 THEN
        SELECT flag INTO flagvalue FROM devices WHERE devices.deviceid = on_login_device.deviceid;

        RAISE NOTICE '%s', (SELECT flag FROM devices WHERE devices.deviceid = on_login_device.deviceid);

        IF flagvalue IS NULL THEN
            savedresult := 'SAVED_FAILS';
            savedresultmessage :=
                    '[' || deviceid || '] FLAG HAS NOT BEEN UPDATED ON LOGIN BECAUSE THIS DEVICEID IS NOT EXISTS';
        ELSE
            IF flagvalue = B'0' THEN

                UPDATE devices
                SET flag        = B'1',
                    updatedflag = CURRENT_TIMESTAMP
                WHERE devices.deviceid = on_login_device.deviceid;

                savedresult := 'SAVED_SUCCESSFULLY';
                savedresultmessage
                    := '[' || deviceid || '] FLAG HAS BEEN UPDATED ON LOGIN';
            ELSE
                savedresult := 'SAVED_FAILS';
                savedresultmessage
                    :=
                        '[' || deviceid || '] FLAG HAS NOT BEEN UPDATED ON LOGIN BECAUSE IT HAS ALREADY BEEN LOGGED IN';
            END IF;
        END IF;

        INSERT INTO device_save_progressing (deviceid, savedstatus, savedresultmessage)
        VALUES (deviceid, savedresult, savedresultmessage)
        RETURNING id INTO progressid;

        RAISE NOTICE '%s | %s | DEVICE SAVE ID PROGRESSING: %s', savedresult, savedresultmessage, progressid;

    ELSE
        savedresult := 'SAVED_FAILS';
        savedresultmessage
            :=
                '[' || deviceid || '] FLAG HAS NOT BEEN UPDATED ON LOGIN BECAUSE DEVICEID IS ALREADY PROGRESSING';
        RAISE NOTICE '%s | %s | DEVICE SAVE ID PROGRESSING: ALREADY IN PROGRESS', savedresult, savedresultmessage;
    END IF;
    RETURN;
END;
$$;

CREATE OR REPLACE FUNCTION on_logout_device(
    deviceid VARCHAR(255),
    OUT savedresult VARCHAR(255),
    OUT savedresultmessage VARCHAR(255),
    OUT progressid BIGINT
)
    RETURNS RECORD
    LANGUAGE plpgsql
AS
$$
DECLARE
    flagvalue BIT;
BEGIN
    IF (SELECT count(*)
        FROM device_save_progressing
        WHERE device_save_progressing.deviceid = on_logout_device.deviceid) =
       0 THEN
        SELECT flag INTO flagvalue FROM devices WHERE devices.deviceid = on_logout_device.deviceid;

        RAISE NOTICE '%s', (SELECT flag FROM devices WHERE devices.deviceid = on_logout_device.deviceid);

        IF flagvalue IS NULL THEN
            savedresult := 'SAVED_FAILS';
            savedresultmessage :=
                    '[' || deviceid || '] FLAG HAS NOT BEEN UPDATED ON LOGIN BECAUSE THIS DEVICEID IS NOT EXISTS';
        ELSE
            IF flagvalue = B'1' THEN

                UPDATE devices
                SET flag        = B'0',
                    updatedflag = CURRENT_TIMESTAMP
                WHERE devices.deviceid = on_logout_device.deviceid;

                savedresult := 'SAVED_SUCCESSFULLY';
                savedresultmessage
                    := '[' || deviceid || '] FLAG HAS BEEN UPDATED ON LOGOUT';
            ELSE
                savedresult := 'SAVED_FAILS';
                savedresultmessage
                    :=
                        '[' || deviceid ||
                        '] FLAG HAS NOT BEEN UPDATED ON LOGOUT BECAUSE IT HAS ALREADY BEEN LOGGED OUT';
            END IF;
        END IF;

        INSERT INTO device_save_progressing (deviceid, savedstatus, savedresultmessage)
        VALUES (deviceid, savedresult, savedresultmessage)
        RETURNING id INTO progressid;

        RAISE NOTICE '%s | %s | DEVICE SAVE ID PROGRESSING: %s', savedresult, savedresultmessage, progressid;

    ELSE
        savedresult := 'SAVED_FAILS';
        savedresultmessage
            :=
                '[' || deviceid || '] FLAG HAS NOT BEEN UPDATED ON LOGOUT BECAUSE DEVICEID IS ALREADY PROGRESSING';
        RAISE NOTICE '%s | %s | DEVICE SAVE ID PROGRESSING: ALREADY IN PROGRESS', savedresult, savedresultmessage;
    END IF;
    RETURN;
END;
$$;

CREATE OR REPLACE FUNCTION poll_device_in_progress(
    deviceid VARCHAR(255),
    OUT savedresult VARCHAR(255),
    OUT savedresultmessage VARCHAR(255),
    OUT progressid BIGINT
)
    RETURNS RECORD
    LANGUAGE plpgsql
AS
$$
DECLARE
    progressrecord RECORD;
BEGIN
    IF 0 < (SELECT count(*)
            FROM device_save_progressing
            WHERE device_save_progressing.deviceid = poll_device_in_progress.deviceid) THEN

        SELECT id, savedstatus, dsp.savedresultmessage
        INTO progressrecord
        FROM device_save_progressing AS dsp
        WHERE dsp.deviceid = poll_device_in_progress.deviceid;

        savedresult := progressrecord.savedstatus;
        savedresultmessage := progressrecord.savedresultmessage;
        progressid := progressrecord.id;

        DELETE FROM device_save_progressing AS dsp WHERE dsp.id = progressid;

        RAISE NOTICE '%s | %s | %s | DATA WAS TAKEN AND SEIZED', progressid, savedresult, savedresultmessage;
    ELSE
        savedresult := 'SAVED_FAILS';
        savedresultmessage := '[' || deviceid || '] NOT FOUND SO THAT DEVICEID IN PROGRESS';
        progressid := NULL;

        RAISE NOTICE '%s | %s | %s | DATA WAS NOT TAKEN AND SEIZED', progressid, savedresult, savedresultmessage;
    END IF;

    RETURN;
END;
$$;
