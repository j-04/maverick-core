package com.maverick.core.config;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.net.URL;
import java.util.*;

public class PathScannerConfig implements Config {
    @Getter
    private final Reflections scanner;

    public PathScannerConfig(List<String> packagesToScan) {
        Collection<URL> urls = new ArrayList<>();
        for (String pkg: packagesToScan) {
            urls.addAll(ClasspathHelper.forPackage(pkg));
        }
        ConfigurationBuilder cb = new ConfigurationBuilder()
                .setUrls(urls);
        this.scanner = new Reflections(cb);
    }

    @Override
    @Nullable
    public <T> Class<? extends T> getImplementation(Class<T> type) {
        Set<Class<? extends T>> impls = scanner.getSubTypesOf(type);
        if (impls.size() > 1) {
            throw new RuntimeException("There is more than one implementation of type " + type);
        }
        if (impls.size() == 0)
            return null;
        return impls.iterator().next();
    }
}
