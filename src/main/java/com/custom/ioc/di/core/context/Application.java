package com.custom.ioc.di.core.context;

import com.custom.ioc.di.core.config.Config;

import java.util.List;

public class Application {
    public static ApplicationContext run(List<Config> configList) {
        return new ApplicationContext(configList);
    }

    public static ApplicationContext run(String[] packagesToScan) {
        return new ApplicationContext(packagesToScan);
    }
}
