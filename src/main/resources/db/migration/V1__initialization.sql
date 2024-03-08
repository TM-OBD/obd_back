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
    deviceId           varchar(255) not null unique,
    savedStatus        varchar(255) not null,
    savedResultMessage varchar(255) not null
);

CREATE
    OR REPLACE FUNCTION onLoginDevice(
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
    IF (SELECT count(*) FROM device_save_progressing WHERE device_save_progressing.deviceId = onLoginDevice.deviceId) = 0 THEN
        SELECT flag INTO flagValue FROM devices WHERE devices.deviceid = onLoginDevice.deviceId;

        RAISE NOTICE '%s', (SELECT flag FROM public.devices WHERE devices.deviceid = onLoginDevice.deviceId);

        IF flagValue IS NULL THEN
            savedresult := 'SAVED_FAILS';
            savedresultmessage :=
                    '[' || deviceid || '] flag HAS NOT been updated on LOGIN because this deviceId is not exists';
        ELSE
            IF flagValue = B'0' THEN
                UPDATE public.devices SET flag = B'1' WHERE devices.deviceid = onLoginDevice.deviceId;
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

CREATE OR REPLACE FUNCTION loginDeviceInProgress(
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
    IF 1 = (SELECT count(*)
            FROM device_save_progressing
            WHERE device_save_progressing.deviceId = loginDeviceInProgress.deviceId) THEN

        SELECT id, savedstatus, dsp.savedresultmessage
        INTO progressRecord
        FROM device_save_progressing AS dsp
        WHERE dsp.deviceId = loginDeviceInProgress.deviceId;

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