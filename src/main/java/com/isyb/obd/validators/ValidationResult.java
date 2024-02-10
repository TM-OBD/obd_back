package com.isyb.obd.validators;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private final List<ValidationError> errors = new ArrayList<>();

    public void add(ValidationError error) {
        this.errors.add(error);
    }

    public void addErrors(List<ValidationError> errors) {
        this.errors.addAll(errors);
    }

    public boolean isValid() {
        return this.errors.isEmpty();
    }

    public List<ValidationError> getErrors() {
        return this.errors;
    }
}
