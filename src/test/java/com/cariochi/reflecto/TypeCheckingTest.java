package com.cariochi.reflecto;

import com.cariochi.reflecto.types.ReflectoType;
import com.cariochi.reflecto.types.Types;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TypeCheckingTest {

    @Test
    void test() {
        ReflectoType type = Reflecto.reflect(Types.listOf(String.class));

        assertThat(type.is(Collection.class)).isTrue();
        assertThat(type.is(Types.type(Collection.class, String.class))).isTrue();
        assertThat(type.is(Types.type(Collection.class, Long.class))).isFalse();

        assertThat(type.as(Iterable.class).arguments().get(0).actualType())
                .isEqualTo(String.class);

        assertThat(type.isAssignableFrom(ArrayList.class)).isTrue();
        assertThat(type.isAssignableFrom(Types.type(ArrayList.class, String.class))).isTrue();
        assertThat(type.isAssignableFrom(Types.type(ArrayList.class, Long.class))).isFalse();

        assertThat(type.isInstance(new ArrayList<>())).isTrue();
    }

}
