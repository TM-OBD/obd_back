package com.isyb.obd.validators;

import reactor.core.publisher.Mono;

public interface Validator<T> {
    ValidationResult validate(T obj);
}
