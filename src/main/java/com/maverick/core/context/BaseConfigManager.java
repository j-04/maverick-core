package com.maverick.core.context;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import com.maverick.core.config.Config;
import com.maverick.core.config.PathScannerConfig;

class BaseConfigManager implements ConfigManager {
    private static final List<String> CORE_PACKAGES;
    static {
        CORE_PACKAGES = List.of(
                "com.maverick.core",
                "com.maverick.core.validator"
        );
    }
    public List<Config> setUpConfigList(List<Config> configs) {
        final Config CORE_CONFIG = new PathScannerConfig(CORE_PACKAGES);
        List<Config> allConfigs = new ArrayList<>(configs);
        allConfigs.add(CORE_CONFIG);
        return allConfigs;
    }

    public List<Config> setUpConfigList(String... packages) {
        List<String> packagesList = new ArrayList<>(Arrays.asList(packages));
        packagesList.addAll(CORE_PACKAGES);
        Config config = new PathScannerConfig(packagesList);
        return List.of(config);
    }
}
