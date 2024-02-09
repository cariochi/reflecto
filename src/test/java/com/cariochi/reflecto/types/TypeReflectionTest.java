package com.cariochi.reflecto.types;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.reflect;
import static com.cariochi.reflecto.types.Types.type;
import static org.assertj.core.api.Assertions.assertThat;

class TypeReflectionTest {

    @Test
    void should_reflect() {

        final Type type = type(Dto.class, Integer.class);

        assertThat(reflect(type).reflect("value").actualType()).isEqualTo(Integer.class);
        assertThat(reflect(type).reflect("child.value").actualType()).isEqualTo(Integer.class);
        assertThat(reflect(type).reflect("child.map[0]").actualType()).isEqualTo(String.class);
        assertThat(reflect(type).reflect("set[0].value").actualType()).isEqualTo(Integer.class);
        assertThat(reflect(type).reflect("child.map[1][0]").actualType()).isEqualTo(type);
        assertThat(reflect(type).reflect("child.map[1][0].value").actualType()).isEqualTo(Integer.class);

    }

    @Test
    void should_cast() {
        final Type type = type(Dto.class, String.class);
        assertThat(reflect(type).as(Iterable.class).actualType())
                .isEqualTo(Types.type(Iterable.class, String.class));
    }


    @Test
    void array_test() {
        final Type type = type(Dto.class, String.class);

        final ReflectoType array = reflect(type).reflect("array");
        assertThat(array.isArray()).isTrue();
        assertThat(array.asArray().componentType().actualType())
                .isEqualTo(Types.setOf(String.class));
    }

    @Test
    void primitive_test() {
        final Type type = type(Dto.class, String.class);

        final ReflectoType reflectoType = reflect(type).reflect("primitive");
        assertThat(reflectoType.actualType())
                .isEqualTo(int.class);
    }

    @Test
    void wildcard_test() {
        final Type type = type(Dto.class, String.class);
        final ReflectoType reflectoType = reflect(type).reflect("wildCardList");
        assertThat(reflectoType.actualType().getTypeName())
                .isEqualTo("java.util.List<?>");

        final Type type1 = new TypeReference<Dto<?>>() {}.getType();
        assertThat(reflect(type1).actualType().getTypeName())
                .isEqualTo("com.cariochi.reflecto.types.TypeReflectionTest$Dto<?>");

        final Type type2 = new TypeReference<List<Dto<?>>>() {}.getType();
        assertThat(reflect(type2).actualType().getTypeName())
                .isEqualTo("java.util.List<com.cariochi.reflecto.types.TypeReflectionTest$Dto<?>>");

    }

    public static class Dto<T> extends ArrayList<T> {

        private T value;
        private Dto<T> child;
        private Set<Dto<T>> set;
        private Map<String, Set<Dto<T>>> map;
        private Set<T>[] array;
        private int primitive;
        private List<?> wildCardList;

    }

}
