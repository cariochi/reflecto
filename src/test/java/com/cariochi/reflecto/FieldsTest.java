package com.cariochi.reflecto;

import com.cariochi.reflecto.dto.TestAnnotation;
import com.cariochi.reflecto.dto.TestDto;
import com.cariochi.reflecto.fields.JavaField;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.cariochi.reflecto.dto.TestDto.dto;
import static org.assertj.core.api.Assertions.assertThat;

class FieldsTest {

    @Test
    void should_get_field() {
        final TestDto dto = dto(1).withChild(dto(2));
        final JavaField text = Reflecto.reflect(dto).get("getChildren()").get("[0]").fields().get("text");
        assertThat(text.getName()).isEqualTo("text");
    }
    @Test
    void should_get_all_fields() {
        final TestDto dto = dto(1).withChild(dto(2));
        final List<JavaField> fields = Reflecto.reflect(dto).get("getChildren()").get("[0]").fields().all();
        assertThat(fields).hasSize(8);
    }

    @Test
    void should_get_fields_with_annotation() {
        final TestDto dto = dto(1).withChild(dto(2));
        final List<JavaField> fields = Reflecto.reflect(dto).get("getChildren().get(?)", 0).fields().withAnnotation(TestAnnotation.class);
        assertThat(fields)
                .extracting(JavaField::getName)
                .containsExactly("text");

        fields.get(0).setValue("Hello!");
        assertThat(dto.getChildren().get(0).getText()).isEqualTo("Hello!");
    }

}
