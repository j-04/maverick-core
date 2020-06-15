package com.custom.ioc.di.core.config;

import lombok.Getter;
import org.reflections.Reflections;

import java.util.Set;

public class JavaConfig implements Config {
    @Getter
    private Reflections scanner;

    public JavaConfig(String packageToScan) {
        this.scanner = new Reflections(packageToScan);
    }

    @Override
    public <T> Class<? extends T> getImplementation(Class<T> type) {
        Set<Class<? extends T>> impls = scanner.getSubTypesOf(type);
        if (impls.size() != 1) {
            throw new RuntimeException("There is zero implementations or more than one implementation of type " + type);
        }
        return impls.iterator().next();
    }
}
