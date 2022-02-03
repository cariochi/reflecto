package com.cariochi.reflecto.types;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.cariochi.reflecto.types.Types.type;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class TypeNameTest {

    private static Stream<Arguments> test() {
        return Stream.of(
                arguments("java.util.List<java.lang.String>", TypeName.of(List.class, String.class)),
                arguments("java.util.ArrayList<java.lang.Integer[]>", TypeName.of(ArrayList.class, Integer[].class)),
                arguments("java.util.ArrayList<int[]>", TypeName.of(ArrayList.class, int[].class)),
                arguments("java.util.ArrayList<int[][]>", TypeName.of(ArrayList.class, int[][].class)),
                arguments("java.util.Map<java.lang.String, java.lang.Integer>", TypeName.of(Map.class, String.class, Integer.class)),
                arguments("java.util.HashMap<java.lang.String, java.lang.Integer>", TypeName.of(HashMap.class, String.class, Integer.class)),
                arguments("java.util.Map$Entry<java.lang.String, java.lang.String>", TypeName.of(Map.Entry.class, String.class, String.class)),
                arguments("java.util.Map$Entry<int[][], java.lang.String[]>", TypeName.of(Map.Entry.class, int[][].class, String[].class)),
                arguments("java.util.List<java.util.Map<java.lang.String, java.lang.Integer>>", TypeName.of(List.class, type(Map.class, String.class, Integer.class))),
                arguments("int[][]", TypeName.of(int[][].class)),
                arguments("java.util.Map$Entry<int[][], java.util.Map$Entry<java.lang.String[], byte[]>>", TypeName.of(Map.Entry.class, int[][].class, type(Map.Entry.class, String[].class, byte[].class))),
                arguments("java.util.List<java.lang.String>[][]", TypeName.of(List[][].class, String.class))
        );
    }

    @ParameterizedTest
    @MethodSource
    void test(String name, TypeName typeName) {
        assertThat(typeName.toString()).isEqualTo(name);
        assertThat(TypeName.parse(name)).isEqualTo(typeName);
    }


}
