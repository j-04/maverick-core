package com.maverick.core.validator;

import com.maverick.core.api.annotation.InjectByType;
import com.maverick.core.exception.IncorrectMobConfigurationException;

import java.lang.reflect.Field;

class MobCyclicDependenciesContextValidator implements ContextValidator {
    @Override
    public <T> void validate( Class<?> classToValidate, Class<T> baseMobClass) {
        Class<?> currentClass = classToValidate;
        while (!currentClass.equals(Object.class)) {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(InjectByType.class)) {
                    final Class<?> fieldType = field.getType();
                    if (fieldType.equals(baseMobClass))
                        throw new IncorrectMobConfigurationException("Found cyclic dependency in a @Mob class: " + baseMobClass.getName() + " ---> " + currentClass.getName());
                    /**
                     * Recursive call
                     */
                    validate(baseMobClass, fieldType);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }
}
