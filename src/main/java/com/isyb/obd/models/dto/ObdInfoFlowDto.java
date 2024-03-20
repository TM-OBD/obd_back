package com.isyb.obd.models.dto;

import com.isyb.obd.models.entities.ObdInfo;
import lombok.Data;

@Data
public class ObdInfoFlowDto {
    private ObdInfoDto obdInfoDto;
    private ObdInfoStatus obdInfoStatus;
    private ObdInfo obdInfo;

    public enum ObdInfoStatus {
        PARSED_SUCCESSFULLY, PARSED_FAILS, FILTER_FLAG_SUCCESSFULLY, FILTER_FLAG_FAILS, FILLED_SUCCESSFULLY, FILLED_FAILS, MAPPED_SUCCESSFULLY, MAPPED_FAILS, SAVED;

        private String errorMessage;

        public String getErrorMessage() {
            return errorMessage;
        }

        public static ObdInfoStatus PARSED_FAILS(String errorMessage) {
            ObdInfoStatus status = PARSED_FAILS;
            status.errorMessage = errorMessage;
            return status;
        }

        public static ObdInfoStatus FILLED_FAILS(String errorMessage) {
            ObdInfoStatus status = FILLED_FAILS;
            status.errorMessage = errorMessage;
            return status;
        }

        public static ObdInfoStatus MAPPED_FAILS(String errorMessage) {
            ObdInfoStatus status = MAPPED_FAILS;
            status.errorMessage = errorMessage;
            return status;
        }

        public static ObdInfoStatus FILTER_FLAG_FAILS(String errorMessage) {
            ObdInfoStatus status = FILTER_FLAG_FAILS;
            status.errorMessage = errorMessage;
            return status;
        }
    }
}
