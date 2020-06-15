package com.custom.ioc.di.core.context;

import com.custom.ioc.di.core.annotation.Lazy;
import com.custom.ioc.di.core.annotation.Singleton;
import com.custom.ioc.di.core.config.Config;
import com.custom.ioc.di.core.config.JavaConfig;
import com.custom.ioc.di.core.factory.ObjectFactory;
import lombok.Getter;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {
    @Getter
    private final Config coreConfig;
    @Getter
    private final Config customConfig;
    private ObjectFactory objectFactory;
    private final Map<Class, Object> singletonCache = new ConcurrentHashMap<>();

    public ApplicationContext(Config customConfig) {
        this.customConfig = customConfig;
        this.coreConfig = new JavaConfig("com.custom.ioc.di.core");
        initContext();
    }

    public <T> T getObject(Class<T> type) {
        Class<? extends T> implClass = type;

        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            implClass = coreConfig.getImplementation(type);

            if (implClass == null)
                implClass = customConfig.getImplementation(type);
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
        initEagerSingletons(coreConfig);
        initEagerSingletons(customConfig);
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
