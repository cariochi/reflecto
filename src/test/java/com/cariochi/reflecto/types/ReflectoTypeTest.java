package com.cariochi.reflecto.types;


import com.cariochi.reflecto.exceptions.NotFoundException;
import com.cariochi.reflecto.types.ReflectoType.EnumConstants;
import com.cariochi.reflecto.types.ReflectoTypeTest.Super.Child;
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
                .hasMessage("Enum value NONE of com.cariochi.reflecto.types.ReflectoTypeTest$MyEnum not found. Available values: [FIRST, SECOND, THIRD]");
    }

    @Test
    void should_report_missing_field_in_type_expression_with_context() {
        assertThatThrownBy(() -> reflect(Super.class).reflect("missing"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Field missing not found while resolving expression missing")
                .hasMessageContaining(Super.class.getName())
                .hasMessageContaining("field");
    }

    @Test
    void should_report_invalid_type_view_with_context() {
        assertThatThrownBy(() -> reflect(String.class).asArray())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Type java.lang.String is not an array");

        assertThatThrownBy(() -> reflect(String.class).asEnum())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Type java.lang.String is not an enum");
    }

    @Test
    void should_get_declared_types() {
        final Type type = Types.type(Super.class, String.class);

        assertThat(reflect(type).types().list())
                .extracting(ReflectoType::actualType)
                .containsExactly(Child.class);

        assertThat(reflect(type).types().declared().list())
                .extracting(ReflectoType::actualType)
                .containsExactly(Child.class);

    }

    public static class Super<T> {
        private T field;

        public static class Child<T, K> extends Super<T> implements Interface<K> {
        }

    }


    private interface Interface<K> {
    }

    private enum MyEnum {
        FIRST, SECOND, THIRD
    }

}
