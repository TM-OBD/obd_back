package com.isyb.obd.validators;

import java.util.List;

public interface ValidatorFacade<T, E> {
    E doValid(T validationObject);
}
