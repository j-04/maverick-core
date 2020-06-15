package com.custom.ioc.di.core.context;

import com.custom.ioc.di.core.configurator.ObjectConfigurator;
import com.custom.ioc.di.core.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

class ObjectFactory {
    private ApplicationContext context;
    List<ObjectConfigurator> configurators = new ArrayList<>();

    public ObjectFactory(ApplicationContext context) {
        this.context = context;
        loadObjectConfigurators();
    }

    private void loadObjectConfigurators() {
        var impls = context.getCoreConfig().getScanner().getSubTypesOf(ObjectConfigurator.class);
        for (Class<? extends ObjectConfigurator> impl : impls) {
            try {
                configurators.add(impl.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public <T> T createObject(Class<T> implClass) {
        T object = null;
        try {
            object = implClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        for (ObjectConfigurator configurator : configurators) {
            configurator.configure(object, context);
        }

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
        return object;
    }
}
