package com.maverick.core.configurator;

public interface ProxyObjectConfigurator {
    <T> T wrapWithProxyIfNecessary(T object, Class<T> nativeClass);
}
