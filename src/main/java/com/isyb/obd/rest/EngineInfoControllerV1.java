package com.isyb.obd.rest;

import com.isyb.obd.models.dto.EngineInfoDto;
import com.isyb.obd.models.entities.EngineInfo;
import com.isyb.obd.models.mapper.EngineInfoMapper;
import com.isyb.obd.models.repos.EngineInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Optional;

import static com.isyb.obd.util.Sources.INFO_V1;

@RestController
@RequiredArgsConstructor
public class EngineInfoControllerV1 {
    @Autowired
    private EngineInfoRepository engineInfoRepository;
    @Autowired
    private EngineInfoMapper engineInfoMapper;

    //    TODO: implement try catch
    @PostMapping(INFO_V1)
    public ResponseEntity<String> handleInfoV1(@RequestBody String payload) {
        EngineInfoDto engineInfoDto = new EngineInfoDto();
        String valueAlreadyExists = "The value is already exists!";
        String[] fields = payload.split(",");

        for (String field : fields) {
            String[] values = field.split(":");

            switch (values[0]) {
                case "0":
                    if (Optional.ofNullable(engineInfoDto.getTimestamp()).isPresent()) {
                        return ResponseEntity.badRequest().body(valueAlreadyExists);
                    }

                    engineInfoDto.setTimestamp(new Timestamp(Long.parseLong(values[1])));

                    break;
                case "a":
                    if (Optional.ofNullable(engineInfoDto.getLatitude()).isPresent()) {
                        return ResponseEntity.badRequest().body(valueAlreadyExists);
                    }

                    engineInfoDto.setLatitude(Double.parseDouble(values[1]));

                    break;
                case "b":
                    if (Optional.ofNullable(engineInfoDto.getLongitude()).isPresent()) {
                        return ResponseEntity.badRequest().body(valueAlreadyExists);
                    }

                    engineInfoDto.setLongitude(Double.parseDouble(values[1]));

                    break;
                case "24":
                    if (Optional.ofNullable(engineInfoDto.getVoltage()).isPresent()) {
                        return ResponseEntity.badRequest().body(valueAlreadyExists);
                    }

                    engineInfoDto.setVoltage(Double.parseDouble(values[1]));

                    break;
                case "82":
                    if (Optional.ofNullable(engineInfoDto.getTemperature()).isPresent()) {
                        return ResponseEntity.badRequest().body(valueAlreadyExists);
                    }

                    engineInfoDto.setTemperature(Double.parseDouble(values[1]));

                    break;
                default:
                    return ResponseEntity.badRequest().body("The argument" + values[0] + " doesn't exist");
            }
        }

//        TODO: в ответе возвращать инфу какие именно пустые поля?
        if (engineInfoDto.fieldsAreNotEmpty()) {
            Optional<EngineInfo> engineInfo = Optional.ofNullable(engineInfoRepository.save(engineInfoMapper.toEntity(engineInfoDto)));

            if (engineInfo.isEmpty()) {
                return ResponseEntity.badRequest().body("Something went wrong, couldn't save the information!");
            }

            return ResponseEntity.ok().body("");
        } else {
            return ResponseEntity.badRequest().body("Empty fields are exist");
        }

    }
}
