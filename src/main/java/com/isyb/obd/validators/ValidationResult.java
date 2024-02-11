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

    public String getStringErrors() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");

        for (ValidationError error : errors) {
            if (error.getWhere() != null) {
                stringBuilder.append("\n[" + error.getIdentifier() + "] " + "(" + error.getWhere().getName() + ") " + error.getDescription());
            } else {
                stringBuilder.append("\n[" + error.getIdentifier() + "] " + error.getDescription());
            }
        }

        stringBuilder.append("\n\n");

        return stringBuilder.toString();
    }
}
