package com.test.ioc.di.test1.package1;

import com.maverick.core.api.annotation.Mob;

import javax.annotation.PostConstruct;

@Mob
public class Person implements IPerson {
    private Person person;

    @PostConstruct
    private void init() {
        person = this;
    }

    public void sayHello() {
        System.out.println("Hello!");
    }
}
