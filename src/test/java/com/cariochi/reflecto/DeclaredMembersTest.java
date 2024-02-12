package com.cariochi.reflecto;

import com.cariochi.reflecto.types.ReflectoType;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.reflect;
import static com.cariochi.reflecto.types.Types.type;
import static org.assertj.core.api.Assertions.assertThat;

class DeclaredMembersTest {

    @Test
    void test() {
        final ReflectoType type = reflect(type(MyList.class, String.class));

        assertThat(type.constructors()).hasSize(1);
        assertThat(type.declared().constructors()).hasSize(2);

        assertThat(type.fields()).hasSizeGreaterThan(1);
        assertThat(type.declared().fields()).hasSize(1);

        assertThat(type.methods()).hasSizeGreaterThan(1);
        assertThat(type.declared().methods()).hasSize(1);
    }

    @RequiredArgsConstructor
    private static class MyList<T> extends ArrayList<T> {

        private final String name;

        private MyList() {
            this("World");
        }

        public String sayHello() {
            return "Hello " + name;
        }

    }

}
