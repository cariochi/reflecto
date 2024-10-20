package com.cariochi.reflecto.types;


import com.cariochi.reflecto.exceptions.NotFoundException;
import com.cariochi.reflecto.types.ReflectoType.EnumConstants;
import java.lang.reflect.Type;
import java.util.List;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.reflect;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReflectoTypeTest {

    @Test
    void should_get_super_types() {
        final Type type = Types.type(Child.class, String.class, Integer.class);

        final Type superType = reflect(type).superType().actualType();
        assertThat(superType)
                .isEqualTo(Types.type(Super.class, String.class));

        final List<Type> superInterfaces = reflect(type).interfaces().stream().map(ReflectoType::actualType).toList();
        assertThat(superInterfaces)
                .containsOnly(Types.type(Interface.class, Integer.class));
    }

    @Test
    void test_enum() {
        final ReflectoType enumReflection = reflect(MyEnum.class);
        assertThat(enumReflection.isEnum()).isTrue();

        final EnumConstants<MyEnum> enumConstants = enumReflection.asEnum().constants();

        assertThat(enumConstants.list())
                .containsExactly(MyEnum.FIRST, MyEnum.SECOND, MyEnum.THIRD);

        assertThat(enumConstants.find("FIRST"))
                .contains(MyEnum.FIRST);

        assertThat(enumConstants.find("first", true))
                .contains(MyEnum.FIRST);

        assertThatThrownBy(() -> enumConstants.get("NONE"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Enum value NONE of MyEnum class not found");
    }

    private static class Super<T> {
        private T field;
    }

    private static class Child<T, K> extends Super<T> implements Interface<K> {
    }

    private interface Interface<K> {
    }

    private enum MyEnum {
        FIRST, SECOND, THIRD
    }

}
