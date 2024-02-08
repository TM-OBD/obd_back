package com.isyb.obd.models.mapper;

import com.isyb.obd.models.dto.EngineInfoDto;
import com.isyb.obd.models.entities.EngineInfo;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface EngineInfoMapper extends IMapper<EngineInfoDto, EngineInfo> {
}
