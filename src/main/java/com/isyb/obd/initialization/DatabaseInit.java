package com.isyb.obd.initialization;

import com.isyb.obd.initialization.exceptions.StopStartup;
import com.isyb.obd.models.entities.EngineInfo;
import com.isyb.obd.models.entities.EngineInfoField;
import com.isyb.obd.models.repos.EngineInfoFieldRepository;
import com.isyb.obd.validators.*;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>Клас <code>DatabaseInit</code> є компонентом Spring, який реалізує інтерфейс <code>ApplicationRunner</code>. Він запускається при старті додатка і виконує ініціалізацію бази даних.</p>
 * <p>Основні кроки роботи класу:</p>
 * <ol>
 * <li>У методі <code>run</code> створюється список об&#39;єктів <code>EngineInfoField</code>, які представляють поля таблиці бази даних.</li>
 * <li>Створюється екземпляр <code>DatabaseInitValidator</code>, який використовується для валідації створених полів.</li>
 * <li>Виконується валідація полів за допомогою <code>DatabaseInitValidator</code>.</li>
 * <li>Якщо валідація не пройшла успішно, то викидається виняток <code>StopStartup</code>, що призводить до зупинки запуску додатка.</li>
 * <li>Після успішної валідації полів відбувається збереження цих полів у базі даних за допомогою <code>engineInfoFieldRepository</code>.</li>
 * </ol>
 * <p>Клас <code>DatabaseInit</code> також містить вкладений клас <code>DatabaseInitValidator</code>, який виконує валідацію полів бази даних. Він додає валідатори до <code>ValidatorCoordinator</code> і викликає метод <code>validateAll</code>, який повертає результати валідації.</p>
 * <p>У методі <code>validate</code> класу <code>CheckColumnInDatabase</code> перевіряється наявність стовпців у базі даних, порівнюючи їх з колонками, представленими у об&#39;єктах <code>EngineInfoField</code>. Якщо існує різниця у кількості стовпців або деякі стовпці відсутні, додаються відповідні помилки до результату валідації.</p>
 * <p><b>Доповнення:</b></p>
 * <p>Це було зроблено для гнучкості у використанні під час парсингу показників, які приходять на наші ендпоінти (наприклад, у цьому випадку інформація про двигун), якщо раптом в інформативну сутність роботи двигуна додаватимуться додаткові поля. Для валідації використовував відповідний патерн, який опишу в іншому місці. Використовував внутрішні приватні класи, щоб простіше було знаходити пов'язану логіку.</p>
 * <p>Під час додавання нового стовпця в таблиці engine_info необхідно так само додати інформацію про цей стовпець в engine_info_fields. Можливо, у майбутньому варто це автоматизувати.</p>
 */
@Component
@Order(1)
public class DatabaseInit implements ApplicationRunner {
    @Autowired
    private final EngineInfoFieldRepository engineInfoFieldRepository;

    private static final Logger log = LogManager.getLogger(DatabaseInit.class);

    public DatabaseInit(EngineInfoFieldRepository engineInfoFieldRepository) {
        this.engineInfoFieldRepository = engineInfoFieldRepository;
    }

    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Step 1: initialization database: сhecking the presence of columns in table engine_info through table engine_info_fields");
        List<EngineInfoField> fields = engineInfoFieldRepository.findAll();

        if (fields.isEmpty()) {
            //        Готуємось занести ці дані в базу даних, але спочатку необхідно провалідувати
            fields.addAll(
                    List.of(
                            new EngineInfoField(1L, "0", "timestamp"),
                            new EngineInfoField(2L, "a", "latitude"),
                            new EngineInfoField(3L, "b", "longitude"),
                            new EngineInfoField(4L, "24", "voltage"),
                            new EngineInfoField(5L, "82", "temperature")
                    )
            );

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

//        Якщо все гаразд, то зберігаємо в таблиці філдів необхідно інформацію, яку далі будемо використовувати в парсінгу
        engineInfoFieldRepository.saveAll(fields);
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
                Set<String> allColumnsByTableName = engineInfoFieldRepository.findAllColumnsByTableName(name);
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
}
