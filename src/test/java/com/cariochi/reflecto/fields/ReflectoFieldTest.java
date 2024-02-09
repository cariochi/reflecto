package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.types.ReflectoType;
import com.cariochi.reflecto.types.Types;
import java.lang.reflect.Type;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.reflect;
import static org.assertj.core.api.Assertions.assertThat;

class ReflectoFieldTest {

    @Test
    void should_get_declaring_type() {
        final Type type = Types.type(Child.class, String.class);
        final Optional<Type> declaringType = reflect(type).fields().find("field")
                .map(ReflectoField::declaringType)
                .map(ReflectoType::actualType);

        assertThat(declaringType)
                .contains(Types.type(Super.class, String.class));
    }

    private static class Super<T> {

        private T field;

    }

    private static class Child<T> extends Super<T> {

    }

}
