package com.cariochi.reflecto.methods;

import com.cariochi.reflecto.invocations.model.Reflection;
import com.cariochi.reflecto.model.Id;
import java.util.List;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.reflect;
import static com.cariochi.reflecto.TestData.bug;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class TargetMethodsTest {

    @Test
    void should_get_method() {
        final Reflection reflection = reflect(bug());
        final List<TargetMethod> methods = List.of(
                reflection.reflect("getWatchers()").reflect("get(?)", 0).methods().find("setUsername(?)", String.class).orElseThrow(),
                reflection.reflect("getWatchers().get(?)", 0).methods().find("setUsername(?)", String.class).orElseThrow()
        );

        assertThat(methods)
                .extracting(TargetMethod::name, method -> method.returnType().actualType())
                .containsOnly(tuple("setUsername", void.class));
    }

    @Test
    void should_get_methods_with_annotation() {
        final List<TargetMethod> methods = reflect(bug())
                .reflect("watchers[0]")
                .methods().stream()
                .filter(method -> method.annotations().contains(Id.class))
                .collect(toList());

        assertThat(methods)
                .extracting(TargetMethod::name, method -> method.returnType().actualType())
                .containsExactly(tuple("getId", Integer.class));
    }

    @Test
    void should_get_all_methods() {
        final List<TargetMethod> methods = reflect(bug()).methods().list();
        assertThat(methods).hasSizeGreaterThan(25);
    }

    @Test
    void should_invoke() {
        final Reflection reflection = reflect(bug());
        assertThat(reflection.<String>perform("reporter.sayHello(?)", "Vadym"))
                .isEqualTo("Hello Vadym from qa");
        assertThat(reflection.reflect("getReporter()").methods().find("sayHello(?)", String.class).map(method -> method.invoke("Vadym")))
                .contains("Hello Vadym from qa");
    }

}
