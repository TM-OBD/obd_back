package com.isyb.obd.models.mapper;

import com.isyb.obd.models.dto.ObdInfoDto;
import com.isyb.obd.models.entities.ObdInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
@Mapper(componentModel = "spring", imports = {Timestamp.class})
public interface ObdInfoMapper {

//    EngineInfoDto toDto(EngineInfo entity);


    @Mapping(source = "timestamp", target = "timestamp", qualifiedByName = "stringToTimestamp")
    ObdInfo toEntity(ObdInfoDto dto) throws NumberFormatException;

    @Named("stringToTimestamp")
    default Timestamp toTimestamp(String timestamp) {
        Timestamp result = null;
        try {
            result = new Timestamp(Long.parseLong(timestamp));
        } catch (NumberFormatException numberFormatException) {
//            return result;
//           FIXME: Плохая практика, надо будет подумать как объяснить используемому ресурсу что произошла ошибка именно из-за маппинга, а не возвращать null и проверять каждый филд сущности на null
            throw numberFormatException;
        }

        return result;
    }
}
