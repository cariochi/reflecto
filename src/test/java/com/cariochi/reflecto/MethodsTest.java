package com.cariochi.reflecto;

import com.cariochi.reflecto.dto.TestAnnotation;
import com.cariochi.reflecto.dto.TestDto;
import com.cariochi.reflecto.methods.JavaMethod;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.cariochi.reflecto.dto.TestDto.dto;
import static org.assertj.core.api.Assertions.assertThat;

class MethodsTest {

    @Test
    void should_get_method() {
        final TestDto dto = dto(1).withChild(dto(2));
        final JavaMethod method = Reflecto.reflect(dto).get("getChildren().get(?)", 0).methods().get("withDependency", String.class, TestDto.class);
        assertThat(method.getName()).isEqualTo("withDependency");

        TestDto result = method.invoke("new", dto(3)).getValue();
        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getDependencies()).hasSize(1);
    }

    @Test
    void should_get_methods_with_annotation() {
        final TestDto dto = dto(1).withChild(dto(2));

        final List<JavaMethod> methods = Reflecto.reflect(dto).get("children[0]").methods().withAnnotation(TestAnnotation.class);
        assertThat(methods)
                .extracting(JavaMethod::getName)
                .containsExactly("withChild");

        methods.get(0).invoke(dto(3));
        assertThat(dto.getChildren().get(0).getChildren())
                .hasSize(1)
                .extracting(TestDto::getId)
                .containsExactly(3);
    }

}
