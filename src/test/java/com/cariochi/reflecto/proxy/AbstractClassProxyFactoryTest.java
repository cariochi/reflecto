package com.cariochi.reflecto.proxy;

import com.cariochi.reflecto.proxy.ProxyFactory.MethodPostProcessor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

class AbstractClassProxyFactoryTest {

    @Test
    void should_create_proxy() {
        final ProxyFactory proxyFactory = ProxyFactory.builder()
                .extendsClass(MyAbstractClass.class)
                .methodInterceptor(MyMethodInterceptor::new)
                .build();

        final MyAbstractClass instance = proxyFactory.create();

        assertThat(instance.toString()).startsWith("com.cariochi.reflecto.proxy.");
        assertThat(instance.hashCode()).isNotZero();
        assertThat(instance.equals(instance)).isTrue();
        assertThat(instance.sayHello("Vadym")).isEqualTo("You called sayHello(Vadym)");
        assertThat(instance.sayHello()).isEqualTo("Hello!!!");
    }

    public static class MyMethodInterceptor implements InvocationHandler, MethodPostProcessor {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return format("You called %s(%s)", method.getName(), Stream.of(args).map(String::valueOf).collect(joining(", ")));
        }

        @Override
        public Object postProcess(Object proxy, Method method, Object[] args, Object result) {
            return result + "!!!";
        }

    }

    public abstract static class MyAbstractClass {

        public abstract String sayHello(String name);

        public String sayHello() {
            return "Hello";
        }

    }

}
