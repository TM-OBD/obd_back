package com.isyb.obd.rest;

import com.isyb.obd.initialization_components.DatabaseInit;
import com.isyb.obd.models.dto.EngineInfoDto;
import com.isyb.obd.models.entities.EngineInfo;
import com.isyb.obd.models.mapper.EngineInfoMapper;
import com.isyb.obd.models.repos.EngineInfoRepository;
import com.isyb.obd.validators.ValidationError;
import com.isyb.obd.validators.ValidationResult;
import com.isyb.obd.validators.Validator;
import com.isyb.obd.validators.ValidatorFacade;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.*;

import static com.isyb.obd.cache.EngineInfoFieldCache._ENGINE_INFO_FIELDS_CACHE;
import static com.isyb.obd.util.Sources.INFO_V1;

@RestController
@RequiredArgsConstructor
public class EngineInfoControllerV1 {
    @Autowired
    private EngineInfoMapper engineInfoMapper;
    @Autowired
    private EngineInfoRepository engineInfoRepository;
    private static final Logger log = LogManager.getLogger(DatabaseInit.class);

    @PostMapping(INFO_V1)
    public ResponseEntity<String> handleInfoV1(@RequestBody String payload) {
        Map<String, String> parse = parse(payload);
        EngineInfoValidator engineInfoValidator = new EngineInfoValidator();
        ValidationResult validationResult = engineInfoValidator.doValid(parse);

        if (validationResult.isValid()) {
//            engineInfoRepository.save(engineInfoValidator.getEngineInfo());

            log.info("Engine info has been added for {}, payload {}", engineInfoValidator.engineInfo.toString(), payload);
            return ResponseEntity.ok().body("Engine info HAS BEEN added " + engineInfoValidator.engineInfo.toString());
        } else {
            log.warn("Engine info HAS NOT been added for {}, payload {}: {}", engineInfoValidator.engineInfo.toString(), payload, validationResult.getStringErrors());
            return ResponseEntity.badRequest().body(validationResult.getStringErrors());
        }
    }

    private Map<String, String> parse(String input) {
        Map<String, String> result = new HashMap<>();
        String[] keyValuePairs = input.split(",");
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2 && !result.containsKey(keyValue[0])) {
                result.put(keyValue[0], keyValue[1]);
            }
        }
        return result;
    }

    @Getter
    private class EngineInfoValidator implements ValidatorFacade<Map<String, String>, ValidationResult> {
        //        private List<ValidationResult> validationResults = new ArrayList<>();
        private EngineInfo engineInfo = new EngineInfo();

        @Override
        public ValidationResult doValid(Map<String, String> validationObject) {
            CheckFillDto handler1 = new CheckFillDto();
            CheckEmptyFields handler2 = new CheckEmptyFields();
            TryMappingToEntity handler3 = new TryMappingToEntity();
            handler1.setNextValidator(handler2);
            handler2.setNextValidator(handler3);
            ValidationResult validate = handler1.validate(validationObject);

            return validate;
        }

        private class CheckFillDto implements Validator<Map<String, String>> {
            private Validator nextValidator;

            public void setNextValidator(Validator validator) {
                this.nextValidator = validator;

            }

            @Override
            public ValidationResult validate(Map<String, String> obj) {
                ValidationResult validationResult = new ValidationResult();
                EngineInfoDto engineInfoDto = new EngineInfoDto();

                Class<? extends EngineInfoDto> aClass = engineInfoDto.getClass();

                for (Map.Entry<String, String> entry : obj.entrySet()) {
                    Optional<String> s = Optional.ofNullable(_ENGINE_INFO_FIELDS_CACHE.get(entry.getKey()));

                    if (s.isPresent()) {
                        Field declaredField;
                        try {
                            declaredField = aClass.getDeclaredField(s.get());
                            declaredField.setAccessible(true);

                            try {
                                declaredField.set(engineInfoDto, entry.getValue());
                            } catch (IllegalAccessException e) {
                                validationResult.add(ValidationError.of("CheckFillDto", e.getMessage()));
                            }

                        } catch (NoSuchFieldException e) {
                            validationResult.add(ValidationError.of("CheckFillDto", e.getMessage()));
                        }
                    }
//                    На випадок, якщо за неіснуючі ключи необхідно буде відхиляти запрос
//                    else {
//                        validationResult.add(ValidationError.of("CheckFillDto", ""));
//                    }
                }

                if (validationResult.isValid()) {
                    if (nextValidator != null) {
                        ValidationResult validate = nextValidator.validate(engineInfoDto);
                        validationResult.addErrors(validate.getErrors());
                    }
                }

                return validationResult;
            }
        }

        private class CheckEmptyFields implements Validator<EngineInfoDto> {

            private Validator nextValidator;

            public void setNextValidator(Validator validator) {
                this.nextValidator = validator;

            }

            @Override
            public ValidationResult validate(EngineInfoDto obj) {
                ValidationResult validationResult = new ValidationResult();
                Field[] declaredFields = obj.getClass().getDeclaredFields();
                List<Field> emptyFields = new ArrayList<>();

                for (Field field : declaredFields) {
                    field.setAccessible(true);


                    Object value = null;
                    try {
                        value = field.get(obj);
                    } catch (IllegalAccessException e) {
                        validationResult.add(ValidationError.of("CheckEmptyFields", e.getMessage()));
                    }

                    if (Optional.ofNullable(value).isEmpty()) {
                        emptyFields.add(field);
                    }
                }

                if (!emptyFields.isEmpty()) {
                    validationResult.add(ValidationError.of("CheckEmptyFields", emptyFields.toString()));
                }

                if (validationResult.isValid()) {
                    if (nextValidator != null) {
                        ValidationResult validate = nextValidator.validate(obj);
                        validationResult.addErrors(validate.getErrors());
                    }
                }

                return validationResult;
            }
        }

        private class TryMappingToEntity implements Validator<EngineInfoDto> {

            private Validator nextValidator;

            public void setNextValidator(Validator validator) {
                this.nextValidator = validator;

            }

            @Override
            public ValidationResult validate(EngineInfoDto obj) {
                ValidationResult validationResult = new ValidationResult();
                try {
                    engineInfo = engineInfoMapper.toEntity(obj);
                } catch (NumberFormatException numberFormatException) {
                    validationResult.add(ValidationError.of("TryMappingToEntity", numberFormatException.getMessage()));
                }

                if (validationResult.isValid()) {
                    if (nextValidator != null) {
                        ValidationResult validate = nextValidator.validate(obj);
                        validationResult.addErrors(validate.getErrors());
                    }
                }

                return validationResult;
            }
        }
    }
}


//        EngineInfoDto engineInfoDto = fillDto(parse);
//        List<Field> fields = checkEmptyFields(engineInfoDto);
//
//        if (fields.isEmpty()) {
//            try {
//                EngineInfo entity = engineInfoMapper.toEntity(engineInfoDto);
//            } catch (NumberFormatException numberFormatException) {
//                throw new NumberFormatException();
//            }
//        } else {
//            String decription = "There is an unfilled field: " + fields.toString();
//            return ResponseEntity.badRequest().body(decription);
//        }


//    private EngineInfoDto fillDto(Map<String, String> parse) {
//        EngineInfoDto engineInfoDto = new EngineInfoDto();
//        Class<? extends EngineInfoDto> aClass = engineInfoDto.getClass();
//
//        for (Map.Entry<String, String> entry : parse.entrySet()) {
//            Optional<String> s = Optional.ofNullable(_ENGINE_INFO_FIELDS_CACHE.get(entry.getKey()));
//
//            if (s.isPresent()) {
//                Field declaredField;
//                try {
//                    declaredField = aClass.getDeclaredField(s.get());
//                    declaredField.setAccessible(true);
//
//                } catch (NoSuchFieldException e) {
//                    throw new RuntimeException(e);
//                }
//
//                try {
//                    declaredField.set(engineInfoDto, entry.getValue());
//                } catch (IllegalAccessException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//
//        return engineInfoDto;
//    }

//private List<Field> checkEmptyFields(EngineInfoDto engineInfoDto) {
//    Field[] declaredFields = engineInfoDto.getClass().getDeclaredFields();
//    List<Field> emptyFields = new ArrayList<>();
//
//    for (Field field : declaredFields) {
//        field.setAccessible(true);
//
//
//        Object value = null;
//        try {
//            value = field.get(engineInfoDto);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//
//        if (Optional.ofNullable(value).isEmpty()) {
//            emptyFields.add(field);
//        }
//
//    }
//
//    return emptyFields;
//}
//}