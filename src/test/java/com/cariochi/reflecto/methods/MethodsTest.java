package com.cariochi.reflecto.methods;

import com.cariochi.recordo.Read;
import com.cariochi.recordo.RecordoExtension;
import com.cariochi.reflecto.Reflection;
import com.cariochi.reflecto.model.Bug;
import com.cariochi.reflecto.model.Id;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static com.cariochi.reflecto.Reflecto.reflect;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ExtendWith(RecordoExtension.class)
class MethodsTest {

    @Test
    void should_get_method(@Read("/bug.json") Bug bug) {
        final JavaMethod method = reflect(bug)
                .get("getWatchers().get(?)", 0)
                .methods()
                .get("setUsername(?)", String.class);

        assertThat(method)
                .extracting(JavaMethod::getName, JavaMethod::getReturnType)
                .containsExactly("setUsername", void.class);
    }

    @Test
    void should_get_methods_with_annotation(@Read("/bug.json") Bug bug) {
        final List<JavaMethod> methods = reflect(bug)
                .get("watchers[0]")
                .methods()
                .withAnnotation(Id.class);

        assertThat(methods)
                .extracting(JavaMethod::getName, JavaMethod::getReturnType)
                .containsExactly(tuple("getId", Integer.class));
    }

    @Test
    void should_invoke(@Read("/bug.json") Bug bug) {
        final Reflection reflection = reflect(bug);
        assertThat(reflection.<String>invoke("reporter.sayHello(?)", "Vadym"))
                .isEqualTo("Hello Vadym from qa");
        assertThat(reflection.get("getReporter()").methods().get("sayHello(?)", String.class).<String>invoke("Vadym"))
                .isEqualTo("Hello Vadym from qa");
    }

}
