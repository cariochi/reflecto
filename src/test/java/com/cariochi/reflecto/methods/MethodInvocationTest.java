package com.cariochi.reflecto.methods;

import com.cariochi.reflecto.model.User;
import com.cariochi.reflecto.types.ReflectoType;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.reflect;
import static org.assertj.core.api.Assertions.assertThat;

public class MethodInvocationTest {

    @Test
    void should_invoke() {
        final User user = User.builder().username("qa").build();

        final ReflectoType reflection = reflect(User.class);

        final ReflectoMethod reflectoMethod = reflection.methods().get("sayHello(?)", String.class);

        assertThat(reflectoMethod.withTarget(user))
                .extracting(method -> method.invoke("Vadym"))
                .isEqualTo("Hello Vadym from qa");

        assertThat(reflectoMethod.withArguments("Vadym").withTarget(user))
                .extracting(TargetMethodInvocation::invoke)
                .isEqualTo("Hello Vadym from qa");

        assertThat(reflectoMethod.withTarget(user).withArguments("Vadym"))
                .extracting(TargetMethodInvocation::invoke)
                .isEqualTo("Hello Vadym from qa");
    }
}
