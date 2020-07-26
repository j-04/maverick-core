package com.maverick.core.validator;

import com.maverick.core.context.ApplicationContext;

import java.util.Set;

public interface ValidatorManager {
    void validate(ApplicationContext context, Set<Class<?>> mobs);
}
