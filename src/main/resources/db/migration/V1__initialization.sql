create table engine_info
(
    id          bigserial not null,
    latitude    float(53) not null,
    longitude   float(53) not null,
    temperature float(53) not null,
    voltage     float(53) not null,
    timestamp   timestamp(6),
    primary key (id)
);
create table engine_info_fields
(
    id         bigserial    not null,
    field_name varchar(255) not null,
    symbol     varchar(255) not null,
    primary key (id)
);
create table devices
(
    id          bigserial    not null primary key,
    deviceId    varchar(255) not null,
    flag        bit          not null,
    createdAt   timestamp(6) not null,
    updatedFlag timestamp(6) not null,
    VIN         varchar(255) not null unique
);