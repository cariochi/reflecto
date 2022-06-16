package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.Reflection;
import com.cariochi.reflecto.model.Bug;
import com.cariochi.reflecto.model.Enclosing;
import com.cariochi.reflecto.model.Id;
import com.cariochi.reflecto.model.User;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.cariochi.reflecto.Reflecto.reflect;
import static com.cariochi.reflecto.TestData.bug;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class FieldsTest {

    @Nested
    class EnclosingTest {
        private final Enclosing enclosing = new Enclosing("bla bla");
        private final Enclosing.NestedClass.SecondNestedClass nested = enclosing.nested.secondNested;
        private final Fields fields = reflect(nested).fields().includeEnclosing();

        @Test
        void should_get_enclosing_class_field() {
            assertThat(fields.all())
                    .extracting(JavaField::getName, JavaField::getValue)
                    .containsExactlyInAnyOrder(
                            tuple("nested", enclosing.nested),
                            tuple("secondNested", enclosing.nested.secondNested),
                            tuple("summary", "bla bla"),
                            tuple("deprecatedString", "has been"),
                            tuple("deprecatedInt", -1));
        }

        @Test
        void should_get_enclosing_class_field_with_annotation() {
            assertThat(fields.withAnnotation(Deprecated.class))
                    .extracting(JavaField::getName, JavaField::getValue)
                    .containsExactlyInAnyOrder(
                            tuple("deprecatedString", "has been"),
                            tuple("deprecatedInt", -1));
        }

        @Test
        void should_get_enclosing_class_field_with_type() {
            assertThat(fields.withType(String.class))
                    .extracting(JavaField::getName, JavaField::getValue)
                    .containsExactlyInAnyOrder(
                            tuple("summary", "bla bla"),
                            tuple("deprecatedString", "has been"));
        }

        @Test
        void should_get_enclosing_class_field_with_type_and_annotation() {
            assertThat(fields.withTypeAndAnnotation(String.class, Deprecated.class))
                    .extracting(JavaField::getName, JavaField::getValue)
                    .containsExactly(tuple("deprecatedString", "has been"));
        }
    }

    @Test
    void should_get_field() {
        final Reflection reflect = reflect(bug());
        final List<JavaField> fields = List.of(
                reflect.get("watchers[?]", 0).fields().field("username"),
                reflect.get("watchers[?]", 0).field("username"),

                reflect.get("watchers[0]").fields().field("username"),
                reflect.get("watchers[0]").field("username")
        );
        assertThat(fields)
                .extracting(JavaField::getName, JavaField::getValue)
                .containsOnly(tuple("username", "developer"));
    }

    @Test
    void should_get_field_value() {
        final Reflection reflection = reflect(bug());
        final List<Object> usernames = List.of(

                reflection.get("watchers").get("[?]", 0).get("username").getValue(),
                reflection.get("watchers").get("[0]").get("username").getValue(),
                reflection.get("watchers[?]", 0).get("username").getValue(),
                reflection.get("watchers[0]").get("username").getValue(),
                reflection.get("watchers[?].username", 0).getValue(),
                reflection.get("watchers[0].username").getValue(),

                reflection.get("watchers").get("[?]", 0).invoke("username"),
                reflection.get("watchers").get("[0]").invoke("username"),
                reflection.get("watchers[?]", 0).invoke("username"),
                reflection.get("watchers[0]").invoke("username"),
                reflection.invoke("watchers[?].username", 0),
                reflection.invoke("watchers[0].username")

        );
        assertThat(usernames)
                .containsOnly("developer");
    }

    @Test
    void should_set_field_value() {
        final Bug bug = bug();
        final Reflection reflection = reflect(bug);

        reflection.get("watchers").get("[?]", 0).get("username").setValue("TEST1");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST1");

        reflection.get("watchers").get("[0]").get("username").setValue("TEST2");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST2");

        reflection.get("watchers[?]", 0).get("username").setValue("TEST3");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST3");

        reflection.get("watchers[0]").get("username").setValue("TEST4");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST4");

        reflection.get("watchers[?].username", 0).setValue("TEST5");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST5");

        reflection.get("watchers[0].username").setValue("TEST6");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST6");

        reflection.get("watchers").get("[?]", 0).invoke("username=?", "TEST7");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST7");

        reflection.get("watchers").get("[0]").invoke("username=?", "TEST8");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST8");

        reflection.get("watchers[?]", 0).invoke("username=?", "TEST9");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST9");

        reflection.get("watchers[0]").invoke("username=?", "TEST10");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST10");

        reflection.invoke("watchers[?].username=?", 0, "TEST11");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST11");

        reflection.invoke("watchers[0].username=?", "TEST12");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST12");
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
