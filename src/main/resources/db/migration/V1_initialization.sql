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

INSERT INTO public.engine_info_fields (id, field_name, symbol)
VALUES (1, 'timestamp', '0');
INSERT INTO public.engine_info_fields (id, field_name, symbol)
VALUES (2, 'latitude', 'a');
INSERT INTO public.engine_info_fields (id, field_name, symbol)
VALUES (3, 'longitude', 'b');
INSERT INTO public.engine_info_fields (id, field_name, symbol)
VALUES (4, 'voltage', '24');
INSERT INTO public.engine_info_fields (id, field_name, symbol)
VALUES (5, 'temperature', '82');
