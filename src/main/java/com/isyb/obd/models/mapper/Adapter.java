package com.isyb.obd.models.mapper;

public interface Adapter<Entity, Dto> {
    Entity toEntity(Dto dto);

    Dto toDto(Entity entity);
}
