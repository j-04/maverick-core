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
    private ObjectFactory objectFactory;
    @Getter
    private final List<Config> configs;
    private static final List<String> CORE_PACKAGES;
    private final Map<Class<?>, Object> SINGLETON_CACHE = new ConcurrentHashMap<>();

    static {
        CORE_PACKAGES = List.of(
                "com.custom.ioc.di.core"
        );
    }

    public ApplicationContext(List<Config> customConfigs) {
        final Config CORE_CONFIG = new JavaConfig(CORE_PACKAGES);
        List<Config> allConfigs = new ArrayList<>(customConfigs);
        allConfigs.add(CORE_CONFIG);
        this.configs = allConfigs;
        initContext();
    }

    public ApplicationContext(String[] packagesToScan) {
        List<String> packagesList = new ArrayList<>(Arrays.asList(packagesToScan));
        packagesList.addAll(CORE_PACKAGES);
        Config config = new JavaConfig(packagesList);
        this.configs = List.of(config);
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

        if (SINGLETON_CACHE.containsKey(implClass))
            return (T) SINGLETON_CACHE.get(implClass);
        else
            SINGLETON_CACHE.put(implClass, object);
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
                    SINGLETON_CACHE.put(type, object);
                }
            }
        }
    }
}
