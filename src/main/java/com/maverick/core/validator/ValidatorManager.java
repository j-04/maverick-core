package com.maverick.core.validator;

import com.maverick.core.context.ApplicationContext;

import java.util.List;

public interface ValidatorManager {
    void validate(ApplicationContext context, List<Class<Object>> mobs);
}
