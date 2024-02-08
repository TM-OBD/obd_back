package com.isyb.obd.models.mapper;

import com.isyb.obd.models.dto.EngineInfoDto;
import com.isyb.obd.models.entities.EngineInfo;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-02-08T16:45:16+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.10 (Amazon.com Inc.)"
)
@Component
public class EngineInfoMapperImpl implements EngineInfoMapper {

    @Override
    public EngineInfoDto toDto(EngineInfo entity) {
        if ( entity == null ) {
            return null;
        }

        EngineInfoDto engineInfoDto = new EngineInfoDto();

        engineInfoDto.setTimestamp( entity.getTimestamp() );
        engineInfoDto.setLatitude( entity.getLatitude() );
        engineInfoDto.setLongitude( entity.getLongitude() );
        engineInfoDto.setVoltage( entity.getVoltage() );
        engineInfoDto.setTemperature( entity.getTemperature() );

        return engineInfoDto;
    }

    @Override
    public EngineInfo toEntity(EngineInfoDto dto) {
        if ( dto == null ) {
            return null;
        }

        EngineInfo engineInfo = new EngineInfo();

        engineInfo.setTimestamp( dto.getTimestamp() );
        if ( dto.getLatitude() != null ) {
            engineInfo.setLatitude( dto.getLatitude() );
        }
        if ( dto.getLongitude() != null ) {
            engineInfo.setLongitude( dto.getLongitude() );
        }
        if ( dto.getVoltage() != null ) {
            engineInfo.setVoltage( dto.getVoltage() );
        }
        if ( dto.getTemperature() != null ) {
            engineInfo.setTemperature( dto.getTemperature() );
        }

        return engineInfo;
    }
}
