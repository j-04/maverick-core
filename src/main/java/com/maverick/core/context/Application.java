package com.maverick.core.context;

import com.maverick.core.config.Config;

import java.util.List;

public class Application {
    public static ApplicationContext run(List<Config> configList) {
        return new ApplicationContext(configList);
    }

    public static ApplicationContext run(String[] packagesToScan) {
        return new ApplicationContext(packagesToScan);
    }
}
