package com.cariochi.reflecto.types;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static com.cariochi.reflecto.types.Types.any;
import static com.cariochi.reflecto.types.Types.anyExtends;
import static com.cariochi.reflecto.types.Types.anySuper;
import static com.cariochi.reflecto.types.Types.arrayOf;
import static com.cariochi.reflecto.types.Types.listOf;
import static com.cariochi.reflecto.types.Types.type;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class TypesTest {

    private static Stream<Arguments> types() {
        return Stream.of(
                arguments("java.lang.String", type(String.class)),
                arguments("java.util.List<java.lang.String>", type(List.class, String.class)),
                arguments("java.util.ArrayList<java.lang.Integer[]>", type(ArrayList.class, Integer[].class)),
                arguments("java.util.ArrayList<int[]>", type(ArrayList.class, int[].class)),
                arguments("java.util.ArrayList<int[][]>", type(ArrayList.class, int[][].class)),
                arguments("java.util.Map<java.lang.String, java.lang.Integer>", type(Map.class, String.class, Integer.class)),
                arguments("java.util.HashMap<java.lang.String, java.lang.Integer>", type(HashMap.class, String.class, Integer.class)),
                arguments("java.util.Map$Entry<java.lang.String, java.lang.String>", type(Map.Entry.class, String.class, String.class)),
                arguments("java.util.Map$Entry<int[][], java.lang.String[]>", type(Map.Entry.class, int[][].class, String[].class)),
                arguments("java.util.List<java.util.Map<java.lang.String, java.lang.Integer>>", type(List.class, type(Map.class, String.class, Integer.class))),
                arguments("int[][]", type(int[][].class)),
                arguments("java.util.Map$Entry<int[][], java.util.Map$Entry<java.lang.String[], byte[]>>", type(Map.Entry.class, int[][].class, type(Map.Entry.class, String[].class, byte[].class))),
                arguments("java.util.List<java.lang.String>[][]", type(List[][].class, String.class)),
                arguments("java.util.List<?>", listOf(any())),
                arguments("java.util.List<? extends java.lang.String>", listOf(anyExtends(String.class))),
                arguments("java.util.List<? super java.lang.String>", listOf(anySuper(String.class)))
        );
    }

    @ParameterizedTest
    @MethodSource("types")
    void test(String name, Type type) {
        assertThat(type)
                .isEqualTo(type(name))
                .extracting(Type::getTypeName)
                .isEqualTo(name);
    }

    @ParameterizedTest
    @MethodSource("types")
    void should_create_array(String name, Type type) {
        final Type array = arrayOf(type);
        assertThat(array.getTypeName())
                .isEqualTo(name + "[]");
    }

}
