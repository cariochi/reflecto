package com.cariochi.reflecto.methods;

import com.cariochi.reflecto.Reflection;
import com.cariochi.reflecto.model.Id;
import java.util.List;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.reflect;
import static com.cariochi.reflecto.TestData.bug;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class MethodsTest {

    @Test
    void should_get_method() {
        final Reflection reflection = reflect(bug());
        final List<JavaMethod> methods = List.of(
                reflection.get("getWatchers()").get("get(?)", 0).methods().method("setUsername(?)", String.class),
                reflection.get("getWatchers()").get("get(?)", 0).method("setUsername(?)", String.class),

                reflection.get("getWatchers().get(?)", 0).methods().method("setUsername(?)", String.class),
                reflection.get("getWatchers().get(?)", 0).method("setUsername(?)", String.class)
        );

        assertThat(methods)
                .extracting(JavaMethod::getName, JavaMethod::getReturnType)
                .containsOnly(tuple("setUsername", void.class));
    }

    @Test
    void should_get_methods_with_annotation() {
        final List<JavaMethod> methods = reflect(bug())
                .get("watchers[0]")
                .methods()
                .withAnnotation(Id.class);

        assertThat(methods)
                .extracting(JavaMethod::getName, JavaMethod::getReturnType)
                .containsExactly(tuple("getId", Integer.class));
    }

    @Test
    void should_get_all_methods() {
        final List<JavaMethod> methods = reflect(bug()).methods().asList();
        assertThat(methods).hasSize(27);
    }

    @Test
    void should_invoke() {
        final Reflection reflection = reflect(bug());
        assertThat(reflection.<String>invoke("reporter.sayHello(?)", "Vadym"))
                .isEqualTo("Hello Vadym from qa");
        assertThat(reflection.get("getReporter()").methods().method("sayHello(?)", String.class).<String>invoke("Vadym"))
                .isEqualTo("Hello Vadym from qa");
    }

}
