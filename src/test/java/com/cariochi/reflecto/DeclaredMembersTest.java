package com.cariochi.reflecto;

import com.cariochi.reflecto.constructors.ReflectoConstructor;
import com.cariochi.reflecto.fields.ReflectoField;
import com.cariochi.reflecto.methods.ReflectoMethod;
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
        assertThat(type.constructors().declared().list())
                .hasSize(2)
                .extracting(ReflectoConstructor::declaringType)
                .containsOnly(type);

        assertThat(type.fields()).hasSizeGreaterThan(1);
        assertThat(type.fields().declared().list())
                .hasSize(1)
                .extracting(ReflectoField::declaringType)
                .containsOnly(type);

        assertThat(type.methods()).hasSizeGreaterThan(1);
        assertThat(type.methods().declared().list())
                .hasSize(2)
                .extracting(ReflectoMethod::declaringType)
                .containsOnly(type);
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

        @Override
        public boolean add(T t) {
            return super.add(t);
        }
    }

}
