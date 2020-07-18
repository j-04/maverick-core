package com.maverick.core.context;

import com.maverick.core.api.annotation.Lazy;
import com.maverick.core.api.annotation.Singleton;
import com.maverick.core.api.context.IApplicationContext;
import com.maverick.core.config.Config;
import lombok.Getter;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext implements IApplicationContext {
    private ObjectFactory objectFactory;
    @Getter
    private final List<Config> configs;
    private final Map<Class<?>, Object> SINGLETON_CACHE = new ConcurrentHashMap<>();
    private final ConfigManager configManager = new BaseConfigManager();

    public ApplicationContext() {
        this("");
    }

    public ApplicationContext(List<Config> customConfigs) {
        this.configs = configManager.setUpConfigList(customConfigs);
    }

    public ApplicationContext(String... packagesToScan) {
        this.configs = configManager.setUpConfigList(packagesToScan);
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

    public void initContext() {
        objectFactory = new ObjectFactory(this);
        objectFactory.initObjectFactory();
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
