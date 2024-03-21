package com.cariochi.reflecto.utils;

import com.cariochi.reflecto.methods.ReflectoMethods;
import com.cariochi.reflecto.types.Types;
import java.util.List;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.reflect;
import static org.assertj.core.api.Assertions.assertThat;

class MethodsUtilsTest {

    @Test
    void test() {
        final ReflectoMethods methods = reflect(Dto.class).methods();

        assertThat(methods.find("setValue", Object.class)).isPresent();
        assertThat(methods.find("setValue", String.class)).isPresent();

        assertThat(methods.find("setStringValue", Object.class)).isEmpty();
        assertThat(methods.find("setStringValue", String.class)).isPresent();

        assertThat(methods.find("setArrayValue", Object[].class)).isPresent();
        assertThat(methods.find("setArrayValue", String[].class)).isPresent();

        assertThat(methods.find("setStringArrayValue", Object[].class)).isEmpty();
        assertThat(methods.find("setStringArrayValue", String[].class)).isPresent();

        assertThat(methods.find("setStringListValue", Types.listOf(Object.class))).isEmpty();
        assertThat(methods.find("setStringListValue", Types.listOf(String.class))).isPresent();
    }

    private interface Dto {

        <T> void setValue(T value);

        <T extends String> void setStringValue(T value);

        <T> void setArrayValue(T... value);

        <T extends String> void setStringArrayValue(T... value);

        <T extends String> void setStringListValue(List<T> value);
    }

}
