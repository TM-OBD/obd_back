package com.isyb.obd.validators;

import lombok.Getter;

@Getter
public final class ValidationError {
    private final String identifier;
    private final String description;

    private ValidationError(String identifier, String description) {
        this.identifier = identifier;
        this.description = description;
    }

    public static ValidationError of(String identifier, String description) {
        return new ValidationError(identifier, description);
    }
}