package com.maverick.core.context;

import com.maverick.core.api.annotation.Lazy;
import com.maverick.core.api.annotation.Mob;
import com.maverick.core.api.annotation.Singleton;
import com.maverick.core.api.context.IApplicationContext;
import com.maverick.core.config.Config;
import com.maverick.core.config.MobScannerConfig;
import com.maverick.core.exception.MobTypeIsNotDeclaredException;
import com.maverick.core.validator.BaseValidatorManager;
import com.maverick.core.validator.ValidatorManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext implements IApplicationContext {
    private ObjectFactory objectFactory;

    private final List<Config> CONFIGS;
    @Getter
    private Config mobConfig;

    private final Map<Class<?>, Object> SINGLETON_CACHE = new ConcurrentHashMap<>();
    private final ConfigManager configManager = new BaseConfigManager();
    private ValidatorManager validatorManager;

    private final Set<Class<?>> MOB_CLASSES = new HashSet<>();

    public ApplicationContext() {
        this("");
    }

    public ApplicationContext(List<Config> customConfigs) {
        this.CONFIGS = configManager.setUpConfigList(customConfigs);
    }

    public ApplicationContext(String... packagesToScan) {
        this.CONFIGS = configManager.setUpConfigList(packagesToScan);
    }

    public void initContext() {
        scanMobObjects();
        initializeMobScannerConfig();
        objectFactory = new ObjectFactory(this);
        validatorManager = new BaseValidatorManager(this);
        validateMobObjects();
        objectFactory.initObjectFactory();
        initEagerSingletons(mobConfig);
    }

    private void scanMobObjects() {
        for (Config config : CONFIGS) {
            Set<Class<?>> typesAnnotatedWith = config.getScanner().getTypesAnnotatedWith(Mob.class);
            MOB_CLASSES.addAll(typesAnnotatedWith);
        }
    }

    private void validateMobObjects() {
        validatorManager.validate(this, this.MOB_CLASSES);
    }

    private void initializeMobScannerConfig() {
        this.mobConfig = new MobScannerConfig(this.MOB_CLASSES);
    }

    @NotNull
    public <T> T getObject(Class<T> type) {
        if (type == null)
            throw new NullPointerException("The null reference of type is not allowed!");

        Class<? extends T> implClass = type;

        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            implClass = mobConfig.getImplementation(type);
            if (implClass == null) {
                throw new MobTypeIsNotDeclaredException("Can not find mob implementation of " + type);
            }
        } else {
            if (!MOB_CLASSES.contains(type))
                throw new MobTypeIsNotDeclaredException(String.format("Mob with type %s is not declared!", type.getName()));
        }

        T object = objectFactory.createObject(implClass);

        if (SINGLETON_CACHE.containsKey(implClass))
            return (T) SINGLETON_CACHE.get(implClass);
        else
            SINGLETON_CACHE.put(implClass, object);
        return object;
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
