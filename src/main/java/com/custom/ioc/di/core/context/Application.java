package com.custom.ioc.di.core.context;

import com.custom.ioc.di.core.config.Config;

public class Application {
    public static ApplicationContext run(Config config) {
        return new ApplicationContext(config);
    }
}
