package com.maverick.core.configurator;

import com.maverick.core.context.ApplicationContext;

public interface ObjectConfigurator {
    void configure(Object o, ApplicationContext context);
}
