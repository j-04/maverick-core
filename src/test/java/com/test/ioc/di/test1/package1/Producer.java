package com.test.ioc.di.test1.package1;

import com.maverick.core.api.annotation.InjectProperty;
import com.maverick.core.api.annotation.Mob;

@Mob
public class Producer {
    @InjectProperty("producerprop")
    public String property;
}
