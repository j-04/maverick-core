package com.maverick.core.config;

import org.reflections.Reflections;

import javax.annotation.Nullable;

public interface Config {
    @Nullable
    <T> Class<? extends T> getImplementation(Class<T> type);
    Reflections getScanner();
}
