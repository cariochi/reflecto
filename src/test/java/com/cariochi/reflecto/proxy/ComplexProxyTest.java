package com.cariochi.reflecto.proxy;

import com.cariochi.reflecto.methods.ReflectoMethod;
import com.cariochi.reflecto.methods.TargetMethod;
import com.cariochi.reflecto.types.ReflectoType;
import lombok.Data;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.proxy;
import static com.cariochi.reflecto.types.Types.type;
import static org.assertj.core.api.Assertions.assertThat;

class ComplexProxyTest {

    @Test
    void testGenericProxyType() {
        final ProxyType proxyType = proxy(type(AbstractDto.class, Double.class), type(HasValue.class, String.class));
        final ReflectoType reflectoType = proxyType.type();

        assertThat(reflectoType.superType().actualType().getTypeName())
                .isEqualTo("com.cariochi.reflecto.proxy.ComplexProxyTest$AbstractDto<java.lang.Double>");

        assertThat(reflectoType.superType().arguments().get(0).actualType())
                .isEqualTo(Double.class);

        assertThat(reflectoType.interfaces())
                .extracting(reflectoInterface -> reflectoInterface.actualType().getTypeName())
                .containsExactly(
                        "com.cariochi.reflecto.proxy.ComplexProxyTest$HasValue<java.lang.String>"
                );

        assertThat(reflectoType.interfaces().get(0).arguments().get(0).actualType())
                .isEqualTo(String.class);
    }

    @Test
    void should_call_implemented_methods() {
        // <ProxyClass> extends AbstractDto<Double> implements HasName, HasHash, HasValue<Integer>
        final AbstractDto<Double> instance = proxy(type(AbstractDto.class, Double.class), HasName.class, HasHash.class, type(HasValue.class, Integer.class))
                .with(DtoProxyHandler.class)
                .getConstructor(String.class, Double.class)
                .newInstance("Name", 50.0);

        final HasName hasName = (HasName) instance;
        assertThat(hasName.getName()).isEqualTo("Name");

        // DtoProxyHandler.getValue() should be invoked
        final HasValue<Integer> hasValue = (HasValue<Integer>) instance;
        assertThat(hasValue.getValue()).isEqualTo(1);

        assertThat(instance.getX()).isEqualTo(50.0);
    }


    @Test
    void should_use_invoke() {
        // <ProxyClass> extends AbstractDto<Double> implements HasName, HasHash, HasValue<String>
        final AbstractDto<Double> instance = proxy(type(AbstractDto.class, Double.class), HasName.class, HasHash.class, type(HasValue.class, String.class))
                .with(DtoProxyHandler.class)
                .getConstructor(String.class, Double.class)
                .newInstance("Name", 50.0);

        final HasHash hasHash = (HasHash) instance;
        assertThat(hasHash.getHash()).isEqualTo("getHash was invoked");

        // DtoProxyHandler.getValue() should NOT be invoked because it has different generic type argument
        final HasValue<String> hasValue = (HasValue<String>) instance;
        assertThat(hasValue.getValue()).isEqualTo("getValue was invoked");
    }

    private static class DtoProxyHandler extends AbstractDto<Double> implements InvocationHandler, HasName, HasValue<Integer> {

        public DtoProxyHandler(String name, Double x) {
            super(name, x);
        }

        @Override
        public Object invoke(Object proxy, ReflectoMethod thisMethod, Object[] args, TargetMethod proceed) {
            if (proceed != null) {
                return proceed.invoke(args);
            }
            return thisMethod.name() + " was invoked";
        }

        @Override
        public Integer getValue() {
            return 1;
        }
    }

    @Data
    public static abstract class AbstractDto<X> {
        private final String name;
        private final X x;
    }

    private interface HasName {
        String getName();
    }

    private interface HasValue<T> {
        T getValue();
    }

    private interface HasHash {
        String getHash();
    }

}
