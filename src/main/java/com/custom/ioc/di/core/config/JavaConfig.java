package com.custom.ioc.di.core.config;

import lombok.Getter;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class JavaConfig implements Config {
    @Getter
    private final Reflections scanner;

    public JavaConfig(List<String> packagesToScan) {
        Collection<URL> urls = packagesToScan.stream()
                .map(pkg -> ClasspathHelper.forPackage(pkg))
                .findFirst()
                .orElse(Collections.emptyList());
        ConfigurationBuilder cb = new ConfigurationBuilder()
                .setUrls(urls);
        this.scanner = new Reflections(cb);
    }

    @Override
    public <T> Class<? extends T> getImplementation(Class<T> type) {
        Set<Class<? extends T>> impls = scanner.getSubTypesOf(type);
        if (impls.size() > 1) {
            throw new RuntimeException("There more than one implementation of type " + type);
        }
        if (impls.size() == 0)
            return null;
        return impls.iterator().next();
    }
}
