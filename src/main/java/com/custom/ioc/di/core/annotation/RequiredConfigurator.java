package com.custom.ioc.di.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The annotation can only been processed for core configurators (com.custom.ioc.di.core.configurator)
 * ObjectFactory will ignore the annotation on custom configurators during them initialization.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredConfigurator {
}
