package com.custom.ioc.di.core.configurator;

import com.custom.ioc.di.core.context.ApplicationContext;

public interface ObjectConfigurator {
    void configure(Object o, ApplicationContext context);
}
