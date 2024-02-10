package com.isyb.obd.validators;

public interface Validator<T> {
    ValidationResult validate(T obj);
}
