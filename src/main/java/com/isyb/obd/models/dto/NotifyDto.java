package com.isyb.obd.models.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.sql.Timestamp;


@Data
public class NotifyDto {
    @NotNull
    @Size(min = 1, max = 255)
    @Id
    private String deviceId;
    @NotNull
    @Size(min = 1, max = 1)
    @Pattern(regexp = "[01]", message = "eventId must contain only 0 or 1")
    private String event;
    @NotNull
    @PastOrPresent
    private Timestamp timestamp;
    @Size(min = 1, max = 255)
    private String vehicleVIN;
    @Transient
    private NotifyStatus notifyStatus;

    public NotifyDto(@NotNull @Size(min = 1, max = 255) String deviceId, @NotNull @Size(min = 1, max = 1) String event, @NotNull @PastOrPresent Timestamp timestamp, @NotNull @Size(min = 1, max = 255) String vehicleVIN) {
        this.deviceId = deviceId;
        this.event = event;
        this.timestamp = timestamp;
        this.vehicleVIN = vehicleVIN;
        this.notifyStatus = NotifyStatus.CREATED;
    }

    public NotifyDto() {
        this.notifyStatus = NotifyStatus.CREATED;
    }

//    public void process() {
//        notifyStatus.process(this);
//    }

    public enum NotifyStatus {
        CREATED, PARSER_SUCCESSFULLY, PARSER_FAILS, VALIDATE_SUCCESSFULLY, VALIDATE_FAILS, SAVE_PROGRESSING, SAVED_FAILS, SAVED_SUCCESSFULLY;

        private String errorMessage;

        NotifyStatus(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        NotifyStatus() {

        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public static NotifyStatus VALIDATE_FAILS(String errorMessage) {
            NotifyStatus status = VALIDATE_FAILS;
            status.errorMessage = errorMessage;
            return status;
        }

        public static NotifyStatus PARSER_FAILS(String errorMessage) {
            NotifyStatus status = PARSER_FAILS;
            status.errorMessage = errorMessage;
            return status;
        }

        public static NotifyStatus SAVED_FAILS(String errorMessage) {
            NotifyStatus status = SAVED_FAILS;
            status.errorMessage = errorMessage;
            return status;
        }
    }

}

//interface NotifyStatus {
//    NotifyDto process(NotifyDto notifyDto);
//}
//
//class CreatedNotifyStatus implements NotifyStatus {
//    @Override
//    public NotifyDto process(NotifyDto notifyDto) {
//        return notifyDto;
//    }
//}
//
//@Component
//class ValidateNotifyStatus implements NotifyStatus {
//
//    @Override
//    public NotifyDto process(NotifyDto notifyDto) {
//        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
//        validatorFactoryBean.afterPropertiesSet();
//        Errors errors = validatorFactoryBean.validateObject(notifyDto);
//
//        if (errors.hasErrors()) {
//            notifyDto.setNotifyStatus(new ValidateFailsNotifyStatus());
//            return notifyDto;
//        }
//
//        notifyDto.setNotifyStatus(new ValidateSuccessfullyNotifyStatus());
//        return notifyDto;
//    }
//}
//
//class ValidateFailsNotifyStatus implements NotifyStatus {
//
//    @Override
//    public NotifyDto process(NotifyDto notifyDto) {
//        return notifyDto;
//    }
//}
//
//class ValidateSuccessfullyNotifyStatus implements NotifyStatus {
//
//    @Override
//    public NotifyDto process(NotifyDto notifyDto) {
//        return notifyDto;
//    }
//}