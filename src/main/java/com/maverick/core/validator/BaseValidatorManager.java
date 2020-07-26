package com.maverick.core.validator;

import com.maverick.core.config.Config;
import com.maverick.core.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BaseValidatorManager implements ValidatorManager {
    private final Config mobConfig;
    private final List<ContextValidator> VALIDATORS = new ArrayList<>();

    public BaseValidatorManager(ApplicationContext context) {
        this.mobConfig = context.getMobConfig();
        scanForValidators();
    }

    @Override
    public void validate(ApplicationContext context, Set<Class<?>> mobs) {
        for (ContextValidator validator : VALIDATORS) {
            for (Class<?> mob : mobs) {
                validator.validate(mob, mob);
            }
        }
    }

    private void scanForValidators() {
        Set<Class<? extends ContextValidator>> resultSet =
                mobConfig.getScanner().getSubTypesOf(ContextValidator.class);

        for (Class<? extends ContextValidator> aClass : resultSet) {
            if (!Modifier.isAbstract(aClass.getModifiers())) {
                try {
                    ContextValidator object = aClass.getDeclaredConstructor().newInstance();
                    VALIDATORS.add(object);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    System.err.println("Caught an exception during instantiation of context validators!");
                    e.printStackTrace();
                }
            }
        }
    }
}
