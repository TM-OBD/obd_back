package com.isyb.obd.validators;

import lombok.Getter;

@Getter
public final class ValidationError {
    private final String identifier;
    private final String description;
    private final Class<?> where;

    private ValidationError(String identifier, String description, Class<?> where) {
        this.identifier = identifier;
        this.description = description;
        this.where = where;
    }

    private ValidationError(String identifier, String description) {
        this.identifier = identifier;
        this.description = description;
        this.where = null;
    }

    public static ValidationError of(String identifier, String description) {
        return new ValidationError(identifier, description);
    }

    public static ValidationError of(String identifier, String description, Class<?> where) {
        return new ValidationError(identifier, description, where);
    }
}