package com.custom.ioc.di.core.configurator;

import com.custom.ioc.di.core.annotation.RequiredConfigurator;
import com.custom.ioc.di.core.annotation.InjectProperty;
import com.custom.ioc.di.core.context.ApplicationContext;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredConfigurator
public class InjectPropertyAnnotationObjectConfigurator implements ObjectConfigurator {
    private final Map<String, String> properties;

    public InjectPropertyAnnotationObjectConfigurator() {
        Stream<String> lines = null;
        try {
            lines = new BufferedReader(new FileReader(ClassLoader.getSystemClassLoader().getResource("application.properties").getPath())).lines();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (lines != null)
            this.properties = lines
                    .map(String::trim)
                    .filter(line -> line.matches("^([a-zA-Z0-9]* *= *[\\w\\-()!]*)$"))
                    .map(line -> line.split(" *= *"))
                    .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
        else
            this.properties = Collections.emptyMap();
    }

    @Override
    public void configure(Object o, ApplicationContext context) {
        for (Field field : o.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(InjectProperty.class)) {
                InjectProperty p = field.getAnnotation(InjectProperty.class);
                String value = p.value();
                String property;
                String propertyName;
                if (value.isEmpty()) {
                    propertyName = field.getName();
                } else {
                    propertyName = value;
                }
                property = properties.get(propertyName);
                determineFieldType(o, field, propertyName, property);
            }
        }
    }

    private void determineFieldType(Object object, Field field, String propertyName, String property) {
        Class<?> fieldType = field.getType();
        if (fieldType.equals(Number.class) ||
            fieldType.equals(Byte.class) ||
            fieldType.equals(Short.class) ||
            fieldType.equals(Integer.class) ||
            fieldType.equals(Long.class)) {
            injectNumberPropertyInField(object, field, property);
        }
        if (fieldType.equals(Character.class))
            injectCharPropertyInField(object, field, property);
        if (fieldType.equals(String.class))
            injectProperty(object, field, property);
    }

    private void injectCharPropertyInField(Object object, Field field, String property) {
        if (property.length() == 1)
            injectProperty(object, field, property.charAt(0));
        else
            throwRuntimeExceptions(property, field.getType());
    }

    private void injectNumberPropertyInField(Object object, Field field, String property) {
        if (StringUtils.isNumeric(property))
            injectProperty(object, field, Integer.parseInt(property));
        else
            throwRuntimeExceptions(property, field.getType());
    }

    private void injectProperty(Object object, Field field, Object property) {
        try {
            field.setAccessible(true);
            field.set(object, property);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void throwRuntimeExceptions(String property, Class<?> fieldType) {
        throw new RuntimeException(String.format("Can not inject property with value: \"%s\" in field with type: \"%s\"", property, fieldType.getName()));
    }
}
