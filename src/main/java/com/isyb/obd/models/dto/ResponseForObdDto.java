package com.isyb.obd.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "result")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ResponseForObdDto.Done.class, name = "done"),
        @JsonSubTypes.Type(value = ResponseForObdDto.Failed.class, name = "failed")
})
public class ResponseForObdDto {
    @Setter
    @JsonIgnore
    private String result;

    private ResponseForObdDto() {
    }

    @Data
    public static class Done extends ResponseForObdDto {
        private String id;

        public Done(String id) {
            super();
            this.id = id;
            super.setResult("done");
        }
    }

    @Data
    public static class Failed extends ResponseForObdDto {
        private String error;

        public Failed(String error) {
            super();
            this.error = error;
            super.setResult("failed");
        }
    }
}
