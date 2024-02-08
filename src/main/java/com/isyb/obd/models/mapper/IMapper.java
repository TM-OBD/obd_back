package com.isyb.obd.models.mapper;

public interface IMapper<Dto, Entity> {
    Dto toDto(Entity entity);

    Entity toEntity(Dto dto);
}