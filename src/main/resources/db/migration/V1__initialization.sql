create table engine_info
(
    id          bigserial not null primary key,
    latitude    float(53) not null,
    longitude   float(53) not null,
    temperature float(53) not null,
    voltage     float(53) not null,
    timestamp   timestamp(6)
);
create table engine_info_fields
(
    id         bigserial    not null primary key,
    field_name varchar(255) not null,
    symbol     varchar(255) not null
);

INSERT INTO engine_info_fields (symbol, field_name)
VALUES ('0', 'timestamp'),
       ('a', 'latitude'),
       ('b', 'longitude'),
       ('24', 'voltage'),
       ('82', 'temperature');

create table devices
(
    id          bigserial    not null primary key,
    deviceId    varchar(255) not null unique,
    flag        bit          not null,
    createdAt   timestamp(6) not null,
    updatedFlag timestamp(6) not null,
    VIN         varchar(255)
);

create table device_save_progressing
(
    id                 bigserial    not null primary key,
    deviceId           varchar(255) not null,
    savedStatus        varchar(255) not null,
    savedResultMessage varchar(255) not null
);

-- CREATE
--     OR REPLACE FUNCTION on_change_flag_of_device(
--     deviceId VARCHAR(255),
--     flag VARCHAR(1),
--     OUT savedresult VARCHAR(255),
--     OUT savedresultmessage VARCHAR(255),
--     OUT progressId bigint
-- )
--     RETURNS RECORD
--     LANGUAGE plpgsql
-- AS
-- $$
-- DECLARE
--     resultOperation RECORD;
-- BEGIN
--     CASE
--         WHEN flag = '1'
--             THEN SELECT savedresult, savedresultmessage, progressId INTO resultOperation FROM on_login_device(deviceId);
--         WHEN flag = '2' THEN SELECT savedresult, savedresultmessage, progressId
--                              INTO resultOperation
--                              FROM on_logout_device(deviceId);
--         ELSE -- Обработка случаев, когда значение flag не равно '1' или '2'
--         savedresult := 'SAVED_FAILS';
--         savedresultmessage :=
--                 '[' || deviceId || '] flag HAS NOT been updated on LOGOUT because this flag type is not exists';
--         progressId := NULL;
--
--         RAISE NOTICE '%s | %s | %s ', savedresult, savedresultmessage, progressId;
--         RETURN;
--         END CASE;
--
--     -- Присваиваем значения выходным параметрам
--     savedresult := resultOperation.savedresult;
--     savedresultmessage := resultOperation.savedresultmessage;
--     progressId := resultOperation.progressId;
--     RETURN;
-- END;
-- $$;


CREATE
    OR REPLACE FUNCTION on_login_device(
    deviceId VARCHAR(255),
    OUT savedresult VARCHAR(255),
    OUT savedresultmessage VARCHAR(255),
    OUT progressId bigint
)
    RETURNS RECORD
    LANGUAGE plpgsql
AS
$$
DECLARE
    flagValue BIT;
BEGIN
    IF (SELECT count(*)
        FROM device_save_progressing
        WHERE device_save_progressing.deviceId = on_login_device.deviceId) = 0 THEN
        SELECT flag INTO flagValue FROM devices WHERE devices.deviceid = on_login_device.deviceId;

        RAISE NOTICE '%s', (SELECT flag FROM devices WHERE devices.deviceid = on_login_device.deviceId);

        IF flagValue IS NULL THEN
            savedresult := 'SAVED_FAILS';
            savedresultmessage :=
                    '[' || deviceid || '] flag HAS NOT been updated on LOGIN because this deviceId is not exists';
        ELSE
            IF flagValue = B'0' THEN

                UPDATE devices
                SET flag        = B'1',
                    updatedflag = CURRENT_TIMESTAMP
                WHERE devices.deviceid = on_login_device.deviceId;

                savedresult := 'SAVED_SUCCESSFULLY';
                savedresultmessage
                    := '[' || deviceid || '] flag has been updated on LOGIN';
            ELSE
                savedresult := 'SAVED_FAILS';
                savedresultmessage
                    :=
                        '[' || deviceid || '] flag HAS NOT been updated on LOGIN because it has already been logged in';
            END IF;
        END IF;

        INSERT INTO device_save_progressing (deviceid, savedstatus, savedresultmessage)
        VALUES (deviceId, savedresult, savedresultmessage)
        RETURNING id INTO progressId;

        RAISE NOTICE '%s | %s | device save id progressing: %s', savedresult, savedresultmessage, progressId;

    ELSE
        savedresult := 'SAVED_FAILS';
        savedresultmessage
            :=
                '[' || deviceid || '] flag HAS NOT been updated on LOGIN because deviceId in already progressing';
        RAISE NOTICE '%s | %s | device save id progressing: ALREADY IN PROGRESS', savedresult, savedresultmessage;
    end if;
    RETURN;
END;
$$;

CREATE
    OR REPLACE FUNCTION on_logout_device(
    deviceid VARCHAR(255),
    OUT savedresult VARCHAR(255),
    OUT savedresultmessage VARCHAR(255),
    OUT progressId bigint
)
    RETURNS RECORD
    LANGUAGE plpgsql
AS
$$
DECLARE
    flagValue BIT;
BEGIN
    IF (SELECT count(*)
        FROM device_save_progressing
        WHERE device_save_progressing.deviceId = on_logout_device.deviceId) =
       0 THEN
        SELECT flag INTO flagValue FROM devices WHERE devices.deviceid = on_logout_device.deviceId;

        RAISE NOTICE '%s', (SELECT flag FROM devices WHERE devices.deviceid = on_logout_device.deviceId);

        IF flagValue IS NULL THEN
            savedresult := 'SAVED_FAILS';
            savedresultmessage :=
                    '[' || deviceid || '] flag HAS NOT been updated on LOGIN because this deviceId is not exists';
        ELSE
            IF flagValue = B'1' THEN

                UPDATE devices
                SET flag        = B'0',
                    updatedflag = CURRENT_TIMESTAMP
                WHERE devices.deviceid = on_logout_device.deviceId;

                savedresult := 'SAVED_SUCCESSFULLY';
                savedresultmessage
                    := '[' || deviceid || '] flag has been updated on LOGOUT';
            ELSE
                savedresult := 'SAVED_FAILS';
                savedresultmessage
                    :=
                        '[' || deviceid ||
                        '] flag HAS NOT been updated on LOGOUT because it has already been logged out';
            END IF;
        END IF;

        INSERT INTO device_save_progressing (deviceid, savedstatus, savedresultmessage)
        VALUES (deviceId, savedresult, savedresultmessage)
        RETURNING id INTO progressId;

        RAISE NOTICE '%s | %s | device save id progressing: %s', savedresult, savedresultmessage, progressId;

    ELSE
        savedresult := 'SAVED_FAILS';
        savedresultmessage
            :=
                '[' || deviceid || '] flag HAS NOT been updated on LOGOUT because deviceId in already progressing';
        RAISE NOTICE '%s | %s | device save id progressing: ALREADY IN PROGRESS', savedresult, savedresultmessage;
    end if;
    RETURN;
END;
$$;

CREATE OR REPLACE FUNCTION poll_device_in_progress(
    deviceid VARCHAR(255),
    OUT savedresult VARCHAR(255),
    OUT savedresultmessage VARCHAR(255),
    OUT progressId bigint
)
    RETURNS RECORD
    LANGUAGE plpgsql
AS
$$
DECLARE
    progressRecord RECORD;
BEGIN
    IF 0 < (SELECT count(*)
            FROM device_save_progressing
            WHERE device_save_progressing.deviceId = poll_device_in_progress.deviceId) THEN

        SELECT id, savedstatus, dsp.savedresultmessage
        INTO progressRecord
        FROM device_save_progressing AS dsp
        WHERE dsp.deviceId = poll_device_in_progress.deviceId;

        savedresult := progressRecord.savedstatus;
        savedresultmessage := progressRecord.savedresultmessage;
        progressId := progressRecord.id;

        DELETE FROM device_save_progressing AS dsp WHERE dsp.id = progressId;

        RAISE NOTICE '%s | %s | %s | Data was taken and seized', progressId, savedresult, savedresultmessage;
    ELSE
        savedresult := 'SAVED_FAILS';
        savedresultmessage := '[' || deviceid || '] not found so that deviceId in progress';
        progressId := NULL;

        RAISE NOTICE '%s | %s | %s | Data WAS NOT taken and seized', progressId, savedresult, savedresultmessage;
    end if;

    RETURN;
end;
$$;