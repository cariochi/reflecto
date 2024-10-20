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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class TypeReferenceTest {

    private static Stream<Arguments> test() {
        return Stream.of(
                arguments("java.util.List<java.lang.String>", new TypeReference<List<String>>() {}),
                arguments("java.util.ArrayList<java.lang.Integer[]>", new TypeReference<ArrayList<Integer[]>>() {}),
                arguments("java.util.ArrayList<int[]>", new TypeReference<ArrayList<int[]>>() {}),
                arguments("java.util.ArrayList<int[][]>", new TypeReference<ArrayList<int[][]>>() {}),
                arguments("java.util.Map<java.lang.String, java.lang.Integer>", new TypeReference<Map<String, Integer>>() {}),
                arguments("java.util.HashMap<java.lang.String, java.lang.Integer>", new TypeReference<HashMap<String, Integer>>() {}),
                arguments("java.util.Map$Entry<java.lang.String, java.lang.String>", new TypeReference<Map.Entry<String, String>>() {}),
                arguments("java.util.Map$Entry<int[][], java.lang.String[]>", new TypeReference<Map.Entry<int[][], String[]>>() {}),
                arguments("java.util.List<java.util.Map<java.lang.String, java.lang.Integer>>", new TypeReference<List<Map<String, Integer>>>() {}),
                arguments("int[][]", new TypeReference<int[][]>() {}),
                arguments("java.util.Map$Entry<int[][], java.util.Map$Entry<java.lang.String[], byte[]>>",
                        new TypeReference<Map.Entry<int[][], Map.Entry<String[], byte[]>>>() {}),
                arguments("java.util.List<java.lang.String>[][]", new TypeReference<List<String>[][]>() {})
        );
    }

    @ParameterizedTest
    @MethodSource
    void test(String name, TypeReference<?> typeReference) {
        assertThat(typeReference.getType())
                .extracting(Type::getTypeName)
                .isEqualTo(name);
    }

}
