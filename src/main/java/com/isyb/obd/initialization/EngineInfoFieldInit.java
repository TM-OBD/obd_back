package com.isyb.obd.initialization;

import com.isyb.obd.initialization.exceptions.StopStartup;
import com.isyb.obd.models.entities.EngineInfo;
import com.isyb.obd.models.entities.EngineInfoField;
import com.isyb.obd.models.repos.EngineInfoFieldRepository;
import com.isyb.obd.validators.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@Order(1)
public class EngineInfoFieldInit implements ApplicationRunner {
    @Autowired
    private final EngineInfoFieldRepository engineInfoFieldRepository;

    private static final Logger log = LogManager.getLogger(EngineInfoFieldInit.class);

    public EngineInfoFieldInit(EngineInfoFieldRepository engineInfoFieldRepository) {
        this.engineInfoFieldRepository = engineInfoFieldRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Step 1: initialization database: сhecking the presence of columns in table engine_info through table engine_info_fields");

        Flux<EngineInfoField> all = engineInfoFieldRepository.findAll();
        List<EngineInfoField> fields = all.collect(Collectors.toList()).block();

        if (fields.isEmpty()) {
            //        Готуємось занести ці дані в базу даних, але спочатку необхідно провалідувати
//            fields.addAll(
//                    List.of(
//                            new EngineInfoField(1L, "0", "timestamp"),
//                            new EngineInfoField(2L, "a", "latitude"),
//                            new EngineInfoField(3L, "b", "longitude"),
//                            new EngineInfoField(4L, "24", "voltage"),
//                            new EngineInfoField(5L, "82", "temperature")
//                    )
//            );

            throw new StopStartup("Fields can not be empty!");

        }
//        Викликаємо метод для валідації
        List<ValidationResult> validationResults = new DatabaseInitValidator().doValid(fields);

//        Перевіряємо результати валідацій
        for (ValidationResult validationResult : validationResults) {
            if (!validationResult.isValid()) {
//                Якщо щось пішло не так, то необхідно закінчити запуск додатку

                log.fatal("Step 1: Stop startup");
                throw new StopStartup("Validation was not passed: " + validationResult.getStringErrors());
            }
        }

////        Якщо все гаразд, то зберігаємо в таблиці філдів необхідно інформацію, яку далі будемо використовувати в парсінгу
//        Flux<EngineInfoField> engineInfoFieldFlux = engineInfoFieldRepository.saveAll(fields);
//        List<EngineInfoField> engineInfoFields = engineInfoFieldFlux.collect(Collectors.toList()).block();
    }

    //    Клас, в якому валідуємо поетапно необхідні дані та збираємо результати цих валідацій
    private class DatabaseInitValidator implements ValidatorFacade<List<EngineInfoField>, List<ValidationResult>> {
        @Override
        public List<ValidationResult> doValid(List<EngineInfoField> validationObject) {
            log.info("Step 1: Beginning of validation");
            ValidatorCoordinator validatorCoordinator = new ValidatorCoordinator<>();
//            Додаємо кастомні валідатори, в яких описано як валідуємо певний обєкт
            validatorCoordinator.addValidator(new CheckColumnInDatabase());

//            Запускаємо процес валідації наших кастомнів валідаторів
            List<ValidationResult> validationResults = validatorCoordinator.validateAll(validationObject);

            return validationResults;
        }

        //    Перевірка існування столбця в таблиці та в списку філдів яки приготували
        private class CheckColumnInDatabase implements Validator<List<EngineInfoField>> {
            @Override
            public ValidationResult validate(final List<EngineInfoField> obj) {
                log.info("Step 1: Checking the existence of a column in the table and in the list of cooked fields");
                ValidationResult validationResult = new ValidationResult();

//                Беремо назву таблиці в базі даних. Зроблено для зручності (DRY)
                Class<EngineInfo> engineInfoClass = EngineInfo.class;
                Table annotation = engineInfoClass.getAnnotation(Table.class);
                String name = annotation.name();

//                З підготовлених даних беремо потрібне нам поле і перетворюємо на кортеж. Кортеж був узятий для зручної роботи з множинами і високої швидкості для завдання
                Set<String> columns = obj.stream()
                        .map(EngineInfoField::getField_name)
                        .collect(Collectors.toCollection(HashSet::new));
//                З information_schema.columns за назвою таблиці дістаємо інформацію про те, які зберігає стовпці, та ігноруємо стовпець id, щоб не було різниці в порівнянні

                Flux<String> allColumnsByTableNameFlux = engineInfoFieldRepository.findAllColumnsByTableName(name);
                Set<String> allColumnsByTableName = allColumnsByTableNameFlux
                        .collect(Collectors.toSet())
                        .block();
                allColumnsByTableName.removeIf("id"::equals);


//                Швиденько перевіряємо умови, які нас цікавлять: відповідність розміру і відповідність вмісту
                boolean containsSameElements = columns.size() == allColumnsByTableName.size() && columns.containsAll(allColumnsByTableName);
                if (containsSameElements) {
//                    Якщо все ок, то відразу повертаємо результат валідації, щоб не навантажувати додатковими перевірками
                    log.info("Step 1: The validation was successful. Dimensions match, fields of the engine_info_fields table contain columns of the engine_info table");
                    return validationResult;
                } else {
//                    Якщо умова якась не виконалася, то навантажуємо додатковими перевірками для більшої інформативності розробника
                    if (columns.size() != allColumnsByTableName.size()) {
                        validationResult.add(ValidationError.of("1", "Different number of columns, columns.size() = " + columns.size() + ", allColumnsByTableName.size() = " + allColumnsByTableName.size(), this.getClass()));
                    }

//                    Якщо columns (підготовлені філди) не містить всі елементи множини allColumnsByTableName, або якщо розміри не співпадають тоді визначаємо які сами елементи
                    if (!columns.containsAll(allColumnsByTableName) || columns.size() != allColumnsByTableName.size()) {
//                        Отримуємо множину елементів які є в в таблиці БД, але нема в підготовлених даних
                        Set<String> missingInColumns = new HashSet<>(allColumnsByTableName);
                        missingInColumns.removeAll(columns);

//                        Отримуємо множину елемнтів які в підготовлених даних, але нема в таблиці БД
                        Set<String> missingInAllColumnsByTableName = new HashSet<>(columns);
                        missingInAllColumnsByTableName.removeAll(allColumnsByTableName);

                        validationResult.add(ValidationError.of("1", "Elements not present in columns but present in allColumnsByTableName: " + missingInColumns + "; Elements not present in allColumnsByTableName but present in column: " + missingInAllColumnsByTableName, this.getClass()));
                    }
                }


                log.fatal("Step 1: Validation was not passed: {}", validationResult.getStringErrors());
                return validationResult;
            }
        }
    }

    /*@Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Step 1: initialization database: сhecking the presence of columns in table engine_info through table engine_info_fields");

        Flux<EngineInfoField> fields = engineInfoFieldRepository.findAll()
                .switchIfEmpty(
                        Flux.just(
                                new EngineInfoField(1L, "0", "timestamp"),
                                new EngineInfoField(2L, "a", "latitude"),
                                new EngineInfoField(3L, "b", "longitude"),
                                new EngineInfoField(4L, "24", "voltage"),
                                new EngineInfoField(5L, "82", "temperature")
                        )
                );

//        Резко пропали результаты ошибок, хотя ничего не изменил. Периодически то появляются, то исчезают
        Flux<ValidationResult> validationResultFlux = new DatabaseInitValidator().doValid(fields);

        validationResultFlux.concatWith(Mono.just(new ValidationResult()))
                .filter(validationResult -> !validationResult.isValid())
                .subscribe(
                        result -> log.info("Step 1: Validation was successful"),
                        error -> {
                            log.fatal("Step 1: Validation was not passed: {}", error.getMessage());
                            throw new StopStartup("Validation was not passed: " + error.getMessage());
                        }
                );

        fields.flatMap(engineInfoFieldRepository::save).subscribe();

    }

    private class DatabaseInitValidator implements ValidatorFacade<Flux<EngineInfoField>, Flux<ValidationResult>> {

        @Override
        public Flux<ValidationResult> doValid(Flux<EngineInfoField> validationObject) {
            log.info("Step 1: Beginning of validation");

            ValidatorCoordinator validatorCoordinator = new ValidatorCoordinator<>();
            validatorCoordinator.addValidator(new CheckColumnInDatabase());

            List<ValidationResult> validationResults = validatorCoordinator.validateAll(validationObject);

            return Flux.fromIterable(validationResults);
        }
    }

    private class CheckColumnInDatabase implements Validator<Flux<EngineInfoField>> {
        @Override
        public Mono<ValidationResult> validate(Flux<EngineInfoField> infoField) {
            log.info("Step 1: Checking the existence of a column in the table and in the list of cooked fields");

            ValidationResult validationResult = new ValidationResult();

            Class<EngineInfo> engineInfoClass = EngineInfo.class;
            Table annotation = engineInfoClass.getAnnotation(Table.class);
            String name = annotation.name();

            HashSet<String> columns = new HashSet<>();
            Mono<HashSet<String>> collectMono = infoField
                    .map(EngineInfoField::getField_name)
                    .collect(Collectors.toCollection(HashSet::new));
            collectMono.subscribe(column -> columns.addAll(column));

            Set<String> allColumnsByTableName = new HashSet<>();
            Flux<String> allColumnsByTableNameFlux = engineInfoFieldRepository.findAllColumnsByTableName(name);

            allColumnsByTableNameFlux.flatMap(columnSet -> {
                if (!columnSet.equals("id")) {
                    // Здесь можно выполнять дополнительные действия с набором столбцов, если это необходимо
                    allColumnsByTableName.add(columnSet);
                }
                return Mono.empty(); // Возвращаем пустой Mono, так как дополнительные действия уже выполнены
            }).subscribe();


            boolean containsSameElements = columns.size() == allColumnsByTableName.size() && columns.containsAll(allColumnsByTableName);

            if (containsSameElements) {
                log.info("Step 1: The validation was successful. Dimensions match, fields of the engine_info_fields table contain columns of the engine_info table");
                return Mono.just(validationResult);
            } else {
                if (columns.size() != allColumnsByTableName.size()) {
                    validationResult.add(ValidationError.of("1", "Different number of columns, columns.size() = " + columns.size() + ", allColumnsByTableName.size() = " + allColumnsByTableName.size(), this.getClass()));
                }

                if (!columns.containsAll(allColumnsByTableName) || columns.size() != allColumnsByTableName.size()) {
                    Set<String> missingInColumns = new HashSet<>(allColumnsByTableName);
                    missingInColumns.removeAll(columns);
                    Set<String> missingInAllColumnsByTableName = new HashSet<>(columns);
                    missingInAllColumnsByTableName.removeAll(allColumnsByTableName);
                    validationResult.add(ValidationError.of("1", "Elements not present in columns but present in allColumnsByTableName: " + missingInColumns + "; Elements not present in allColumnsByTableName but present in column: " + missingInAllColumnsByTableName, this.getClass()));
                }
            }
            log.fatal("Step 1: Validation was not passed: {}", validationResult.getStringErrors());
            return Mono.just(validationResult);
        }

    }*/
}
