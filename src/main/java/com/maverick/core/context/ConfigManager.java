package com.maverick.core.context;

import com.maverick.core.config.Config;

import java.util.List;

interface ConfigManager {
    List<Config> setUpConfigList(List<Config> configs);
    List<Config> setUpConfigList(String... packages);
}
