package com.maverick.core.context;

import com.maverick.core.annotation.CoreConfigurator;
import com.maverick.core.api.configurator.ObjectConfigurator;
import com.maverick.core.api.configurator.ProxyObjectConfigurator;
import com.maverick.core.config.Config;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class ObjectFactory {
    private final ApplicationContext context;
    private final List<Config> configs;
    private final List<ObjectConfigurator> configurators = new ArrayList<>();
    private final List<ProxyObjectConfigurator> proxyConfigurators = new ArrayList<>();

    public ObjectFactory(ApplicationContext context) {
        this.context = context;
        this.configs = context.getConfigs();
    }

    public void initObjectFactory() {
        loadObjectConfigurators();
        loadProxyObjectConfigurators();
    }

    private void loadObjectConfigurators() {
        final Class<CoreConfigurator> annotation = CoreConfigurator.class;

        for (Config config : configs) {
            Set<Class<? extends ObjectConfigurator>> implsSet = config.getScanner().getSubTypesOf(ObjectConfigurator.class);
            implsSet.stream().filter(impl -> impl.isAnnotationPresent(annotation)).forEach(impl -> {
                try {
                    configurators.add(impl.getDeclaredConstructor().newInstance());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            });
            implsSet.stream().filter(impl -> !impl.isAnnotationPresent(annotation)).forEach(impl -> {
                configurators.add(context.getObject(impl));
            });
        }
    }

    private void loadProxyObjectConfigurators() {
        final Class<CoreConfigurator> annotation = CoreConfigurator.class;

        for (Config config : configs) {
            Set<Class<? extends ProxyObjectConfigurator>> implsSet = config.getScanner().getSubTypesOf(ProxyObjectConfigurator.class);
            implsSet.stream().filter(impl -> impl.isAnnotationPresent(annotation)).forEach(impl -> {
                try {
                    proxyConfigurators.add(impl.getDeclaredConstructor().newInstance());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            });
            implsSet.stream().filter(impl -> !impl.isAnnotationPresent(annotation)).forEach(impl -> {
                proxyConfigurators.add(context.getObject(impl));
            });
        }
    }

    public <T> T createObject(Class<T> implClass) {
        T object = null;

        try {
            object = implClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        configureObject(object);
        callPostConstructMethod(implClass, object);
        return (T) wrapWithProxyIfNecessary(object, implClass);
    }

    private <T> Object wrapWithProxyIfNecessary(T object, Class<T> aClass) {
        for (ProxyObjectConfigurator proxyConfigurator : proxyConfigurators) {
            object = proxyConfigurator.wrapWithProxyIfNecessary(object, aClass);
        }
        return object;
    }

    private <T> void callPostConstructMethod(Class<T> implClass, T object) {
        for (Method declaredMethod : implClass.getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(PostConstruct.class)) {
                if (Modifier.isPublic(declaredMethod.getModifiers()))
                    if (declaredMethod.getParameterCount() == 0) {
                        try {
                            declaredMethod.invoke(object);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
            }
        }
    }

    private <T> void configureObject(T object) {
        for (ObjectConfigurator configurator : configurators) {
            configurator.configure(object, context);
        }
    }
}
