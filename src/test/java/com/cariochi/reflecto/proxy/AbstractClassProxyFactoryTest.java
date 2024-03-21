package com.cariochi.reflecto.proxy;

import com.cariochi.reflecto.methods.ReflectoMethod;
import com.cariochi.reflecto.methods.TargetMethod;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.proxy;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

class AbstractClassProxyFactoryTest {

    @Test
    void should_create_proxy() {
        final MyAbstractClass instance = proxy(MyAbstractClass.class)
                .with(MyProxyHandler.class)
                .getConstructor(int.class)
                .newInstance(1);

        assertThat(instance.toString()).startsWith("MyProxyHandler{}");
        assertThat(instance.hashCode()).isNotZero();
        assertThat(instance.equals(instance)).isTrue();
        assertThat(instance.sayHello("Vadym")).isEqualTo("Hello Vadym");
        assertThat(instance.sayHello()).isEqualTo("Hello");
    }

    @EqualsAndHashCode(callSuper = false)
    public static class MyProxyHandler extends MyAbstractClass implements InvocationHandler {

        public MyProxyHandler(int foo) {
            super(foo);
        }

        @Override
        public Object invoke(Object proxy, ReflectoMethod thisMethod, Object[] args, TargetMethod proceed) {
            if (proceed != null) {
                return proceed.invoke(args);
            }
            return format("You called %s(%s)", thisMethod.name(), Stream.of(args).map(String::valueOf).collect(joining(", ")));
        }

        @Override
        public String sayHello(String name) {
            return "Hello " + name;
        }

        @Override
        public String toString() {
            return "MyProxyHandler{}";
        }
    }

    public abstract static class MyAbstractClass {

        public MyAbstractClass(int foo) {
        }

        public abstract String sayHello(String name);

        public String sayHello() {
            return "Hello";
        }
    }

}
