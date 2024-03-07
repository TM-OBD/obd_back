package com.isyb.obd.validators;

import java.util.ArrayList;
import java.util.List;

public class ValidatorCoordinator<T> {
    private List<Validator<T>> validators;

    public ValidatorCoordinator() {
        this.validators = new ArrayList<>();
    }

    public void addValidator(Validator<T> validator) {
        validators.add(validator);
    }

    public List<ValidationResult> validateAll(T object) {
        List<ValidationResult> validationResults = new ArrayList<>();
        for (Validator<T> validator : validators) {
            validationResults.add(validator.validate(object));
        }

        return validationResults;
    }
}
