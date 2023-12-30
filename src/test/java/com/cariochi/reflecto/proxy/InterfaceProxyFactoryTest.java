package com.cariochi.reflecto.proxy;

import com.cariochi.reflecto.proxy.ProxyFactory.MethodHandler;
import com.cariochi.reflecto.proxy.ProxyFactory.MethodProceed;
import java.lang.reflect.Method;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

class InterfaceProxyFactoryTest {

    @Test
    void should_create_proxy() {
        final MyInterface instance = ProxyFactory.createInstance(new MyMethodHandler(), MyInterface.class);

        assertThat(instance.toString()).startsWith("com.cariochi.reflecto.proxy.");
        assertThat(instance.hashCode()).isNotZero();
        assertThat(instance.equals(instance)).isTrue();
        assertThat(instance.sayHello("Vadym")).isEqualTo("You called sayHello(Vadym)");
        assertThat(instance.sayHello()).isEqualTo("Hello!!!");
    }

    public static class MyMethodHandler implements MethodHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args, MethodProceed proceed) throws Throwable {
            if (method.getDeclaringClass().equals(Object.class)) {
                return proceed.proceed();
            }

            if (proceed == null) {
                return format("You called %s(%s)", method.getName(), Stream.of(args).map(String::valueOf).collect(joining(", ")));
            } else {
                return proceed.proceed() + "!!!";
            }
        }

    }

    public interface MyInterface {

        String sayHello(String name);

        default String sayHello() {
            return "Hello";
        }

    }

}