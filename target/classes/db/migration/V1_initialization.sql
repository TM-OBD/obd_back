create table engine_info
(
    id          bigserial not null,
    latitude    float(53) not null,
    longitude   float(53) not null,
    temperature float(53) not null,
    voltage     float(53) not null,
    timestamp   timestamp(6),
    primary key (id)
)