package com.cariochi.reflecto.types;

import com.cariochi.reflecto.Reflecto;
import com.cariochi.reflecto.fields.ReflectoField;
import com.cariochi.reflecto.fields.TargetField;
import com.cariochi.reflecto.methods.ReflectoMethod;
import com.cariochi.reflecto.methods.TargetMethod;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StaticInvokeTest {

    @SneakyThrows
    @Test
    void should_invoke_static_field() {
        final ReflectoType type = Reflecto.reflect(Dto.class);
        final TargetField staticField = type.fields().find("CONST").map(ReflectoField::asStatic).orElseThrow();
        staticField.setValue("NEW VALUE");

        final Object staticValue = staticField.getValue();
        assertThat(staticValue)
                .isEqualTo("NEW VALUE");
    }

    @Test
    void should_invoke_static_method() {
        final ReflectoType type = Reflecto.reflect(Dto.class);
        final TargetMethod staticMethod = type.methods().find("sayHello", String.class).map(ReflectoMethod::asStatic).orElseThrow();

        final String result = staticMethod.invoke("World");
        assertThat(result)
                .isEqualTo("Hello, World");
    }

    public static class Dto {

        private static String CONST = "CONST";

        private String test;

        private static String sayHello(String name) {
            return "Hello, " + name;
        }

    }

}
