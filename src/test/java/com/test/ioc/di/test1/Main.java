package com.test.ioc.di.test1;

import com.maverick.core.context.Application;
import com.maverick.core.context.ApplicationContext;
import com.maverick.core.exception.MobTypeIsNotDeclaredException;
import com.test.ioc.di.test1.package1.*;
import org.junit.Assert;
import org.junit.Test;

public class Main {
    ApplicationContext context = Application.run("");

    @Test
    public void testGetObject() {
        Person object = context.getObject(Person.class);
        Assert.assertNotNull(object);
    }

    @Test
    public void testGetObjectViaInterface() {
        IPerson object = context.getObject(IPerson.class);
        Assert.assertNotNull(object);
    }

    @Test
    public void testPostConstruct() {
        Service object = context.getObject(Service.class);
        Assert.assertEquals("Post construct!", object.injectionByPostConstruct);
    }

    @Test
    public void testPassNullToGetObject() {
        Assert.assertThrows(NullPointerException.class, () -> context.getObject(null));
    }

    @Test
    public void testGetNotExistingMobObject() {
        Assert.assertThrows(MobTypeIsNotDeclaredException.class, () -> context.getObject(NotMobObject.class));
    }

    @Test
    public void testInjectProperty() {
        Producer object = context.getObject(Producer.class);
        Assert.assertEquals("Test property injection", object.property);
    }

    @Test
    public void testInjectionOfNotExistingProperty() {
        Consumer object = context.getObject(Consumer.class);
        Assert.assertNull(object.consumerProperty);
    }

    @Test
    public void testBadTypeInjection() {
        Assert.assertThrows(RuntimeException.class, () -> context.getObject(DataSource.class));
    }
}
