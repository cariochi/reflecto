package com.cariochi.reflecto.proxy;

import com.cariochi.reflecto.methods.ReflectoMethod;
import com.cariochi.reflecto.methods.TargetMethod;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.proxy;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

class InterfaceProxyFactoryTest {

    @Test
    void should_create_proxy() {
        final MyInterface instance = proxy(MyInterface.class)
                .with(MyProxyHandler::new)
                .getConstructor()
                .newInstance();

        assertThat(instance.toString()).startsWith("com.cariochi.reflecto.proxy.InterfaceProxyFactoryTest$MyInterface_$$");
        assertThat(instance.hashCode()).isNotZero();
        assertThat(instance.equals(instance)).isTrue();
        assertThat(instance.sayHello("Vadym")).isEqualTo("Hello Vadym");
        assertThat(instance.sayHello()).isEqualTo("Hello");
    }

    public static class MyProxyHandler implements MyInterface, InvocationHandler {

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

    }

    private interface MyInterface {
        String sayHello(String name);
        default String sayHello() {
            return "Hello";
        }
    }

}
