package com.cariochi.reflecto.utils;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.types.Types.any;
import static com.cariochi.reflecto.types.Types.anyExtends;
import static com.cariochi.reflecto.types.Types.anySuper;
import static com.cariochi.reflecto.types.Types.arrayOf;
import static com.cariochi.reflecto.types.Types.type;
import static org.assertj.core.api.Assertions.assertThat;

class TypesUtilsTest {

    @Test
    void testIsAssignable() {
        assertThat(TypesUtils.isAssignable(ArrayList.class, List.class, true)).isTrue();

        assertThat(TypesUtils.isAssignable(type(ArrayList.class, String.class), type(List.class, String.class), true)).isTrue();
        assertThat(TypesUtils.isAssignable(type(ArrayList.class, String.class), type(List.class, Integer.class), true)).isFalse();

        assertThat(TypesUtils.isAssignable(type(ArrayList.class, String.class), type(List.class, any()), true)).isTrue();

        assertThat(TypesUtils.isAssignable(type(ArrayList.class, String.class), type(List.class, anyExtends(String.class)), true)).isTrue();
        assertThat(TypesUtils.isAssignable(type(ArrayList.class, String.class), type(List.class, anyExtends(Integer.class)), true)).isFalse();

        assertThat(TypesUtils.isAssignable(type(ArrayList.class, String.class), type(List.class, anySuper(String.class)), true)).isTrue();
        assertThat(TypesUtils.isAssignable(type(ArrayList.class, String.class), type(List.class, anySuper(Integer.class)), true)).isFalse();

        assertThat(TypesUtils.isAssignable(type(ArrayList.class, any()), type(List.class, String.class), true)).isFalse();
        assertThat(TypesUtils.isAssignable(type(ArrayList.class, anyExtends(String.class)), type(List.class, String.class), true)).isFalse();

        assertThat(TypesUtils.isAssignable(arrayOf(type(ArrayList.class, String.class)), arrayOf(type(List.class, String.class)), true)).isTrue();
        assertThat(TypesUtils.isAssignable(arrayOf(type(ArrayList.class, String.class)), arrayOf(type(List.class, any())), true)).isTrue();

    }

}
