package com.maverick.core.context;

import com.maverick.core.config.Config;

import java.util.List;

public class Application {
    public static ApplicationContext run(List<Config> configList) {
        ApplicationContext context = new ApplicationContext(configList);
        context.initContext();
        return context;
    }

    public static ApplicationContext run(String... packagesToScan) {
        ApplicationContext context = new ApplicationContext(packagesToScan);
        context.initContext();
        return context;
    }
}
