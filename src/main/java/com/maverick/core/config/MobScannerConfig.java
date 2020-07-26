package com.maverick.core.config;

import org.reflections.Reflections;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Set;

public class MobScannerConfig implements Config {
    private final Reflections scanner;

    public MobScannerConfig(Set<?> mobObjects) {
        scanner = new Reflections(new ArrayList<>(mobObjects));
    }

    @Override
    @Nullable
    public <T> Class<? extends T> getImplementation(Class<T> type) {
        Set<Class<? extends T>> mobs = scanner.getSubTypesOf(type);
        if (mobs.size() > 1)
            throw new RuntimeException("There is more than one implementation of type " + type);
        if (mobs.size() == 0)
            return null;
        return mobs.iterator().next();
    }

    @Override
    public Reflections getScanner() {
        return this.scanner;
    }
}
