package com.maverick.core.configurator;

import com.maverick.core.annotation.InjectByType;
import com.maverick.core.annotation.CoreConfigurator;
import com.maverick.core.api.configurator.ObjectConfigurator;
import com.maverick.core.api.context.IApplicationContext;

import java.lang.reflect.Field;
import java.util.Objects;

@CoreConfigurator
public class InjectByTypeAnnotationObjectConfiguration implements ObjectConfigurator {
    @Override
    public void configure(Object o, IApplicationContext context) {
        Objects.requireNonNull(o);
        Objects.requireNonNull(context);

        Class<?> oClass = o.getClass();
        while (!oClass.equals(Object.class)) {
            for (Field declaredField : oClass.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(InjectByType.class)) {
                    Class<?> value = declaredField.getAnnotation(InjectByType.class).value();
                    try {
                        if (value.equals(Object.class)) {
                            Class<?> fieldClass = declaredField.getType();
                            Object object = context.getObject(fieldClass);
                            declaredField.setAccessible(true);
                            declaredField.set(o, object);
                        } else {
                            Object object = context.getObject(value);
                            declaredField.setAccessible(true);
                            declaredField.set(o, object);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            oClass = oClass.getSuperclass();
        }
    }
}
