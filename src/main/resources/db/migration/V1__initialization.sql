create table engine_info
(
    latitude    float(53) not null,
    longitude   float(53) not null,
    temperature float(53) not null,
    voltage     float(53) not null,
    id          bigserial not null,
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
