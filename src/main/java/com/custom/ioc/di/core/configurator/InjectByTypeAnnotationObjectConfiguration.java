package com.custom.ioc.di.core.configurator;

import com.custom.ioc.di.core.annotation.RequiredConfigurator;
import com.custom.ioc.di.core.annotation.InjectByType;
import com.custom.ioc.di.core.context.ApplicationContext;

import java.lang.reflect.Field;

@RequiredConfigurator
public class InjectByTypeAnnotationObjectConfiguration implements ObjectConfigurator {
    @Override
    public void configure(Object o, ApplicationContext context) {
        for (Field declaredField : o.getClass().getDeclaredFields()) {
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
    }
}
