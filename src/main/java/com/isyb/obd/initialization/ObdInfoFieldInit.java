package com.isyb.obd.initialization;

import com.isyb.obd.initialization.exceptions.StopStartup;
import com.isyb.obd.models.entities.ObdInfo;
import com.isyb.obd.models.entities.ObdInfoField;
import com.isyb.obd.models.repos.ObdInfoFieldRepository;
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
public class ObdInfoFieldInit implements ApplicationRunner {
    @Autowired
    private final ObdInfoFieldRepository obdInfoFieldRepository;

    private static final Logger log = LogManager.getLogger(ObdInfoFieldInit.class);

    public ObdInfoFieldInit(ObdInfoFieldRepository obdInfoFieldRepository) {
        this.obdInfoFieldRepository = obdInfoFieldRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Step 1: initialization database: сhecking the presence of columns in table engine_info through table obd_info_fields");

        Flux<ObdInfoField> all = obdInfoFieldRepository.findAll();
        List<ObdInfoField> fields = all.collect(Collectors.toList()).block();

        if (fields.isEmpty()) {

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
    }

    //    Клас, в якому валідуємо поетапно необхідні дані та збираємо результати цих валідацій
    private class DatabaseInitValidator implements ValidatorFacade<List<ObdInfoField>, List<ValidationResult>> {
        @Override
        public List<ValidationResult> doValid(List<ObdInfoField> validationObject) {
            log.info("Step 1: Beginning of validation");
            ValidatorCoordinator validatorCoordinator = new ValidatorCoordinator<>();
//            Додаємо кастомні валідатори, в яких описано як валідуємо певний обєкт
            validatorCoordinator.addValidator(new CheckColumnInDatabase());

//            Запускаємо процес валідації наших кастомнів валідаторів
            List<ValidationResult> validationResults = validatorCoordinator.validateAll(validationObject);

            return validationResults;
        }

        //    Перевірка існування столбця в таблиці та в списку філдів яки приготували
        private class CheckColumnInDatabase implements Validator<List<ObdInfoField>> {
            @Override
            public ValidationResult validate(final List<ObdInfoField> obj) {
                log.info("Step 1: Checking the existence of a column in the table and in the list of cooked fields");
                ValidationResult validationResult = new ValidationResult();

//                Беремо назву таблиці в базі даних. Зроблено для зручності (DRY)
                Class<ObdInfo> obdInfoClass = ObdInfo.class;
                Table annotation = obdInfoClass.getAnnotation(Table.class);
                String name = annotation.name();

//                З підготовлених даних беремо потрібне нам поле і перетворюємо на кортеж. Кортеж був узятий для зручної роботи з множинами і високої швидкості для завдання
                Set<String> columns = obj.stream()
                        .map(ObdInfoField::getField_name)
                        .collect(Collectors.toCollection(HashSet::new));
//                З information_schema.columns за назвою таблиці дістаємо інформацію про те, які зберігає стовпці, та ігноруємо стовпець id, щоб не було різниці в порівнянні

                Flux<String> allColumnsByTableNameFlux = obdInfoFieldRepository.findAllColumnsByTableName(name);
                Set<String> allColumnsByTableName = allColumnsByTableNameFlux
                        .collect(Collectors.toSet())
                        .block();
                allColumnsByTableName.removeIf("id"::equals);
                allColumnsByTableName.removeIf("device_id"::equals);


//                Швиденько перевіряємо умови, які нас цікавлять: відповідність розміру і відповідність вмісту
                boolean containsSameElements = columns.size() == allColumnsByTableName.size() && columns.containsAll(allColumnsByTableName);
                if (containsSameElements) {
//                    Якщо все ок, то відразу повертаємо результат валідації, щоб не навантажувати додатковими перевірками
                    log.info("Step 1: The validation was successful. Dimensions match, fields of the obd_info_fields table contain columns of the engine_info table");
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
}
