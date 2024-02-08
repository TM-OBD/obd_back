package com.isyb.obd.models.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Data
public class EngineInfoDto {
    private Timestamp timestamp;
    private Double latitude;
    private Double longitude;
    private Double voltage;
    private Double temperature;

    public boolean fieldsAreNotEmpty() {
//        Можно ещё просто сверять, ссылается ли объект на null - так будет производительнее
//        if (
//                Optional.ofNullable(timestamp).isEmpty() ||
//                        Optional.ofNullable(latitude).isEmpty() ||
//                        Optional.ofNullable(longitude).isEmpty() ||
//                        Optional.ofNullable(voltage).isEmpty() ||
//                        Optional.ofNullable(temperature).isEmpty()
//        ) {
//            return true;
//        }

        Field[] declaredFields = this.getClass().getDeclaredFields();
        List<Field> emptyFields = new ArrayList<>();

        for (Field field : declaredFields) {
            field.setAccessible(true);

            try {
                Object value = field.get(this);

                if (Optional.ofNullable(value).isEmpty()) {
                    emptyFields.add(field);
                }

            } catch (IllegalAccessException e) {
//                Несмотря на то что имеем доступ к приватным полям - все равно обрабатываем ошибку на всякий случай.
                e.printStackTrace();
            }
        }

        if (emptyFields.isEmpty()) {
            return true;
        }

        return false;
    }

}
