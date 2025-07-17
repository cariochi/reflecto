package com.cariochi.reflecto.constructors;

import com.cariochi.reflecto.types.ReflectoType;
import com.cariochi.reflecto.types.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.reflect;
import static org.assertj.core.api.Assertions.assertThat;

class ReflectoConstructorsTest {

  @Test
  void test() {
    final ReflectoType reflectoType = reflect(Types.type(ArrayList.class, String.class));

    final ReflectoConstructors constructors = reflectoType.constructors();
    assertThat(constructors.list()).hasSize(3);
    final ReflectoConstructor constructor = constructors.get(Collection.class);
    assertThat(constructor).isNotNull();

    assertThat(constructor.modifiers().isPublic()).isTrue();
    assertThat(constructor.modifiers().value()).isEqualTo(1);
    assertThat(constructor.declaringType()).isEqualTo(reflectoType);
    assertThat(constructor.name()).isEqualTo("java.util.ArrayList");
    assertThat(constructor.parameters().list()).hasSize(1);
    assertThat(constructor.exceptions().list()).isEmpty();
    assertThat(constructor.toGenericString()).isEqualTo("public java.util.ArrayList(java.util.Collection<? extends E>)");

    ArrayList<String> list = constructor.newInstance(Set.of("a", "b", "c"));
    assertThat(list).hasSize(3);
    assertThat(constructor.annotations().list()).isEmpty();
  }

}
