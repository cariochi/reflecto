package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.types.Types;
import java.lang.reflect.Type;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.reflect;
import static org.assertj.core.api.Assertions.assertThat;

class ReflectoFieldTest {

    @Test
    void should_get_declaring_type() {
        final Type type = Types.type(Child.class, String.class);
        final Type declaringType = reflect(type).fields().get("field").declaringType().actualType();

        assertThat(declaringType)
                .isEqualTo(Types.type(Super.class, String.class));
    }

    private static class Super<T> {
        private T field;
    }

    private static class Child<T> extends Super<T> {
    }

}
