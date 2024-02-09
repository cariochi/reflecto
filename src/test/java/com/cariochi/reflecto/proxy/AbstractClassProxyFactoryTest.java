package com.cariochi.reflecto.proxy;

import com.cariochi.reflecto.methods.ReflectoMethod;
import com.cariochi.reflecto.methods.TargetMethod;
import com.cariochi.reflecto.proxy.ProxyFactory.MethodHandler;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

class AbstractClassProxyFactoryTest {

    @Test
    void should_create_proxy() {
        final MyAbstractClass instance = ProxyFactory.createInstance(new MyMethodHandler(), MyAbstractClass.class);

        assertThat(instance.toString()).startsWith("com.cariochi.reflecto.proxy.");
        assertThat(instance.hashCode()).isNotZero();
        assertThat(instance.equals(instance)).isTrue();
        assertThat(instance.sayHello("Vadym")).isEqualTo("You called sayHello(Vadym)");
        assertThat(instance.sayHello()).isEqualTo("Hello!!!");
    }

    public static class MyMethodHandler implements MethodHandler {

        @Override
        public Object invoke(Object proxy, ReflectoMethod thisMethod, Object[] args, TargetMethod proceed) throws Throwable {

            if (thisMethod.declaringType().actualClass().equals(Object.class)) {
                return proceed.invoke(args);
            }

            if (proceed == null) {
                return format("You called %s(%s)", thisMethod.name(), Stream.of(args).map(String::valueOf).collect(joining(", ")));
            } else {
                return proceed.invoke(args) + "!!!";
            }
        }

    }

    public abstract static class MyAbstractClass {

        public abstract String sayHello(String name);

        public String sayHello() {
            return "Hello";
        }

    }

}
