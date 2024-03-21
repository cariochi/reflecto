package com.cariochi.reflecto.proxy;

import com.cariochi.reflecto.methods.ReflectoMethod;
import com.cariochi.reflecto.methods.TargetMethod;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.proxy;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

class InvocationHandlerTest {

    @Test
    void test() {
        final MyAbstractClass instance = proxy(MyAbstractClass.class)
                .with(MyProxyHandler::new)
                .getConstructor(int.class)
                .newInstance(1);

        assertThat(instance.toString()).startsWith("com.cariochi.reflecto.proxy.InvocationHandlerTest$MyAbstractClass_$$");
        assertThat(instance.hashCode()).isNotZero();
        assertThat(instance.equals(instance)).isTrue();
        assertThat(instance.sayHello("Vadym")).isEqualTo("You called sayHello(Vadym)");
        assertThat(instance.sayHello()).isEqualTo("Hello");
    }

    public static class MyProxyHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, ReflectoMethod thisMethod, Object[] args, TargetMethod proceed) {
            if (proceed != null) {
                return proceed.invoke(args);
            }
            return format("You called %s(%s)", thisMethod.name(), Stream.of(args).map(String::valueOf).collect(joining(", ")));
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
