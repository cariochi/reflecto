package com.cariochi.reflecto.types;


import java.lang.reflect.Type;
import java.util.List;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.reflect;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class ReflectoTypeTest {

    @Test
    void should_get_super_types() {
        final Type type = Types.type(Child.class, String.class, Integer.class);

        final Type superType = reflect(type).superType().actualType();
        assertThat(superType)
                .isEqualTo(Types.type(Super.class, String.class));

        final List<Type> superInterfaces = reflect(type).interfaces().stream().map(ReflectoType::actualType).collect(toList());
        assertThat(superInterfaces)
                .containsOnly(Types.type(Interface.class, Integer.class));
    }

    private static class Super<T> {

        private T field;

    }

    private static class Child<T, K> extends Super<T> implements Interface<K> {

    }

    private interface Interface<K> {

    }

}
