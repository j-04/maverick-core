package com.custom.ioc.di.core.context;

import com.custom.ioc.di.core.annotation.Lazy;
import com.custom.ioc.di.core.annotation.Singleton;
import com.custom.ioc.di.core.config.Config;
import com.custom.ioc.di.core.config.JavaConfig;
import lombok.Getter;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {
    @Getter
    private final List<Config> configs = new ArrayList<>();
    private ObjectFactory objectFactory;
    private final Map<Class<?>, Object> singletonCache = new ConcurrentHashMap<>();

    public ApplicationContext(Config customConfig) {
        configs.add(new JavaConfig("com.custom.ioc.di.core"));
        configs.add(customConfig);
        initContext();
    }

    public <T> T getObject(Class<T> type) {
        Class<? extends T> implClass = type;

        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            for (Config config : configs) {
                implClass = config.getImplementation(type);
                if (implClass != null)
                    break;
            }

            implClass = configs.stream().map(config -> {
                Class<? extends T> impl = config.getImplementation(type);
                return impl;
            }).filter(Objects::nonNull).findFirst().orElse(null);

            Objects.requireNonNull(implClass, "Can not find implementation of interface " + type);
        }

        T object = objectFactory.createObject(implClass);

        if (singletonCache.containsKey(implClass))
            return (T) singletonCache.get(implClass);
        else
            singletonCache.put(implClass, object);
        return object;
    }

    private void initContext() {
        objectFactory = new ObjectFactory(this);
        configs.forEach(this::initEagerSingletons);
    }

    private void initEagerSingletons(Config config) {
        Set<Class<?>> types = config.getScanner().getTypesAnnotatedWith(Singleton.class);
        for (Class<?> type : types) {
            if (!type.isInterface() && !type.isAnnotation() && !Modifier.isAbstract(type.getModifiers())) {
                if (!type.isAnnotationPresent(Lazy.class)) {
                    Object object = objectFactory.createObject(type);
                    singletonCache.put(type, object);
                }
            }
        }
    }
}
