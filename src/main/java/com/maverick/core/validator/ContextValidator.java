package com.maverick.core.validator;

public interface ContextValidator {
    <T> void validate(Class<?> classToValidate, Class<T> baseMobClass);
}
