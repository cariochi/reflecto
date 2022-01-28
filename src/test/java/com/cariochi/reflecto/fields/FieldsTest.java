package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.Reflection;
import com.cariochi.reflecto.model.Bug;
import com.cariochi.reflecto.model.Id;
import com.cariochi.reflecto.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.cariochi.reflecto.Reflecto.reflect;
import static com.cariochi.reflecto.TestData.bug;
import static org.assertj.core.api.Assertions.assertThat;

class FieldsTest {

    @Test
    void should_get_field() {
        final JavaField field = reflect(bug())
                .get("watchers[0]")
                .fields()
                .get("username");

        assertThat(field)
                .extracting(JavaField::getName, JavaField::getValue)
                .containsExactly("username", "developer");
    }

    @Test
    void should_get_all_fields() {
        final List<JavaField> fields = reflect(bug())
                .get("watchers[0]")
                .fields()
                .all();

        assertThat(fields).hasSize(2);
    }

    @Test
    void should_get_fields_with_type() {
        final List<JavaField> fields = reflect(bug())
                .get("getWatchers().get(?)", 0)
                .fields()
                .withType(String.class);

        assertThat(fields)
                .hasSize(1)
                .extracting(JavaField::getName)
                .containsExactly("username");
    }

    @Test
    void should_get_fields_with_annotation() {
        final List<JavaField> fields = reflect(bug())
                .get("getWatchers().get(?)", 0)
                .fields()
                .withAnnotation(Id.class);

        assertThat(fields)
                .hasSize(1)
                .extracting(JavaField::getName)
                .containsExactly("id");
    }

    @Test
    void should_get_fields_with_type_and_annotation() {
        final List<JavaField> fields = reflect(bug())
                .get("getWatchers().get(?)", 0)
                .fields()
                .withTypeAndAnnotation(Integer.class, Id.class);

        assertThat(fields)
                .hasSize(1)
                .extracting(JavaField::getName)
                .containsExactly("id");
    }

    @Test
    void should_invoke() {
        final Bug bug = bug();
        final Reflection reflection = reflect(bug);

        final String username = reflection.invoke("reporter.username");
        assertThat(username).isEqualTo("qa");

        reflection.invoke("reporter.username=?", "TEST");
        assertThat(bug.getReporter().getUsername()).isEqualTo("TEST");

        reflection.invoke("watchers[0]=?", User.builder().id(103).username("watcher").build());
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("watcher");

        reflection.invoke("watchers[?]=?", 1, User.builder().id(104).username("watcher2").build());
        assertThat(bug.getWatchers().get(1).getUsername()).isEqualTo("watcher2");

        reflection.invoke("details[?]=?", "Sprint", "SPR-002");
        assertThat(bug.getDetails().get("Sprint")).isEqualTo("SPR-002");

    }

}
