package com.isyb.obd.models.mapper;

import com.isyb.obd.models.dto.EngineInfoDto;
import com.isyb.obd.models.entities.EngineInfo;
import java.sql.Timestamp;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-02-11T04:09:00+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.10 (Amazon.com Inc.)"
)
@Component
public class EngineInfoMapperImpl implements EngineInfoMapper {

    @Override
    public EngineInfo toEntity(EngineInfoDto dto) throws NumberFormatException {
        if ( dto == null ) {
            return null;
        }

        EngineInfo engineInfo = new EngineInfo();

        engineInfo.setTimestamp( toTimestamp( dto.getTimestamp() ) );
        if ( dto.getLatitude() != null ) {
            engineInfo.setLatitude( Double.parseDouble( dto.getLatitude() ) );
        }
        if ( dto.getLongitude() != null ) {
            engineInfo.setLongitude( Double.parseDouble( dto.getLongitude() ) );
        }
        if ( dto.getVoltage() != null ) {
            engineInfo.setVoltage( Double.parseDouble( dto.getVoltage() ) );
        }
        if ( dto.getTemperature() != null ) {
            engineInfo.setTemperature( Double.parseDouble( dto.getTemperature() ) );
        }

        return engineInfo;
    }
}
