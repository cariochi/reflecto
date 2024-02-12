package com.cariochi.reflecto.methods;

import com.cariochi.reflecto.model.Id;
import com.cariochi.reflecto.model.Name;
import com.cariochi.reflecto.types.ReflectoType;
import com.cariochi.reflecto.types.Types;
import java.lang.reflect.Type;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.reflect;
import static org.assertj.core.api.Assertions.assertThat;

class ReflectoMethodTest {

    @Test
    void should_get_declaring_type() {
        final Type type = Types.type(Child.class, String.class);
        final Optional<Type> declaringType = reflect(type).methods().find("method()")
                .map(ReflectoMethod::declaringType)
                .map(ReflectoType::actualType);

        assertThat(declaringType)
                .contains(Types.type(Super.class, String.class));
    }

    @Test
    void should_find_with_annotation() {
        final Type type = Types.type(Child.class, String.class);
        final ReflectoMethods methods = reflect(type).methods();
        assertThat(methods.stream().filter(method -> method.annotations().contains(Id.class))).isNotEmpty();
        assertThat(methods.stream().filter(method -> method.declared().annotations().contains(Id.class))).isEmpty();
        assertThat(methods.stream().filter(method -> method.annotations().contains(Name.class))).isNotEmpty();
        assertThat(methods.stream().filter(method -> method.declared().annotations().contains(Name.class))).isNotEmpty();
    }

    private static class Super<T> implements Interface<T>, Interface2<T> {

        @Name
        public T method() {
            return null;
        }

    }

    private static class Child<T> extends Super<T> {

    }

    private interface Interface<T> {

        @Id
        T method();

    }

    private interface Interface2<T> {

        T method();

    }

}
