package com.cariochi.reflecto.fields;

import com.cariochi.recordo.Read;
import com.cariochi.recordo.RecordoExtension;
import com.cariochi.reflecto.model.Bug;
import com.cariochi.reflecto.model.Id;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static com.cariochi.reflecto.Reflecto.reflect;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(RecordoExtension.class)
class FieldsTest {

    @Test
    void should_get_field(@Read("/bug.json") Bug bug) {
        final JavaField field = reflect(bug)
                .get("watchers[0]")
                .fields()
                .get("username");

        assertThat(field)
                .extracting(JavaField::getName, JavaField::getValue)
                .containsExactly("username", "developer");
    }

    @Test
    void should_get_all_fields(@Read("/bug.json") Bug bug) {
        final List<JavaField> fields = reflect(bug)
                .get("watchers[0]")
                .fields()
                .all();

        assertThat(fields).hasSize(2);
    }

    @Test
    void should_get_fields_with_type(@Read("/bug.json") Bug bug) {
        final List<JavaField> fields = reflect(bug)
                .get("getWatchers().get(?)", 0)
                .fields()
                .withType(String.class);

        assertThat(fields)
                .hasSize(1)
                .extracting(JavaField::getName)
                .containsExactly("username");
    }

    @Test
    void should_get_fields_with_annotation(@Read("/bug.json") Bug bug) {
        final List<JavaField> fields = reflect(bug)
                .get("getWatchers().get(?)", 0)
                .fields()
                .withAnnotation(Id.class);

        assertThat(fields)
                .hasSize(1)
                .extracting(JavaField::getName)
                .containsExactly("id");
    }

    @Test
    void should_get_fields_with_type_and_annotation(@Read("/bug.json") Bug bug) {
        final List<JavaField> fields = reflect(bug)
                .get("getWatchers().get(?)", 0)
                .fields()
                .withTypeAndAnnotation(Integer.class, Id.class);

        assertThat(fields)
                .hasSize(1)
                .extracting(JavaField::getName)
                .containsExactly("id");
    }

}
