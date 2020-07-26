package com.test.ioc.di.test1.package1;

import com.maverick.core.api.annotation.Mob;

import javax.annotation.PostConstruct;

@Mob
public class Service {
    public String injectionByPostConstruct;

    @PostConstruct
    public void init() {
        this.injectionByPostConstruct = "Post construct!";
    }

    public void doServiceWork() {
        System.out.println("Some service work...");
    }
}
