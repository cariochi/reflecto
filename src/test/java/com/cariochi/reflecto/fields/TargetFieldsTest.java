package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.invocations.model.Reflection;
import com.cariochi.reflecto.model.Bug;
import com.cariochi.reflecto.model.Id;
import com.cariochi.reflecto.model.User;
import java.util.List;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.reflect;
import static com.cariochi.reflecto.TestData.bug;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class TargetFieldsTest {

    @Test
    void should_get_field() {
        final Reflection reflect = reflect(bug());
        final List<TargetField> fields = List.of(
                reflect.reflect("watchers[?]", 0).fields().find("username").orElseThrow(),
                reflect.reflect("watchers[0]").fields().find("username").orElseThrow()
        );
        assertThat(fields)
                .extracting(TargetField::name, TargetField::getValue)
                .containsOnly(tuple("username", "developer"));
    }

    @Test
    void should_get_field_value() {
        final Reflection reflection = reflect(bug());
        final List<Object> usernames = List.of(

                reflection.reflect("watchers").reflect("[?]", 0).reflect("username").getValue(),
                reflection.reflect("watchers").reflect("[0]").reflect("username").getValue(),
                reflection.reflect("watchers[?]", 0).reflect("username").getValue(),
                reflection.reflect("watchers[0]").reflect("username").getValue(),
                reflection.reflect("watchers[?].username", 0).getValue(),
                reflection.reflect("watchers[0].username").getValue(),

                reflection.reflect("watchers").reflect("[?]", 0).perform("username"),
                reflection.reflect("watchers").reflect("[0]").perform("username"),
                reflection.reflect("watchers[?]", 0).perform("username"),
                reflection.reflect("watchers[0]").perform("username"),
                reflection.perform("watchers[?].username", 0),
                reflection.perform("watchers[0].username")

        );
        assertThat(usernames)
                .containsOnly("developer");
    }

    @Test
    void should_set_field_value() {
        final Bug bug = bug();
        final Reflection reflection = reflect(bug);

        reflection.reflect("watchers").reflect("[?]", 0).reflect("username").setValue("TEST1");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST1");

        reflection.reflect("watchers").reflect("[0]").reflect("username").setValue("TEST2");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST2");

        reflection.reflect("watchers[?]", 0).reflect("username").setValue("TEST3");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST3");

        reflection.reflect("watchers[0]").reflect("username").setValue("TEST4");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST4");

        reflection.reflect("watchers[?].username", 0).setValue("TEST5");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST5");

        reflection.reflect("watchers[0].username").setValue("TEST6");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST6");

        reflection.reflect("watchers").reflect("[?]", 0).perform("username=?", "TEST7");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST7");

        reflection.reflect("watchers").reflect("[0]").perform("username=?", "TEST8");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST8");

        reflection.reflect("watchers[?]", 0).perform("username=?", "TEST9");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST9");

        reflection.reflect("watchers[0]").perform("username=?", "TEST10");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST10");

        reflection.perform("watchers[?].username=?", 0, "TEST11");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST11");

        reflection.perform("watchers[0].username=?", "TEST12");
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("TEST12");
    }

    @Test
    void should_get_all_fields() {
        final List<TargetField> fields = reflect(bug())
                .reflect("watchers[0]")
                .fields()
                .list();

        assertThat(fields).hasSize(2);
    }

    @Test
    void should_get_fields_with_type() {
        final List<TargetField> fields = reflect(bug())
                .reflect("getWatchers().get(?)", 0)
                .fields()
                .withType(String.class)
                .list();

        assertThat(fields)
                .hasSize(1)
                .extracting(TargetField::name)
                .containsExactly("username");
    }

    @Test
    void should_get_fields_with_annotation() {
        final List<TargetField> fields = reflect(bug())
                .reflect("getWatchers().get(?)", 0)
                .fields()
                .withAnnotation(Id.class)
                .list();

        assertThat(fields)
                .hasSize(1)
                .extracting(TargetField::name)
                .containsExactly("id");
    }

    @Test
    void should_get_fields_with_type_and_annotation() {
        final List<TargetField> fields = reflect(bug())
                .reflect("getWatchers().get(?)", 0)
                .fields()
                .withType(Integer.class)
                .withAnnotation(Id.class)
                .list();

        assertThat(fields)
                .hasSize(1)
                .extracting(TargetField::name)
                .containsExactly("id");
    }

    @Test
    void should_invoke() {
        final Bug bug = bug();
        final Reflection reflection = reflect(bug);

        final String username = reflection.perform("reporter.username");
        assertThat(username).isEqualTo("qa");

        reflection.perform("reporter.username=?", "TEST");
        assertThat(bug.getReporter().getUsername()).isEqualTo("TEST");

        reflection.perform("watchers[0]=?", User.builder().id(103).username("watcher").build());
        assertThat(bug.getWatchers().get(0).getUsername()).isEqualTo("watcher");

        reflection.perform("watchers[?]=?", 1, User.builder().id(104).username("watcher2").build());
        assertThat(bug.getWatchers().get(1).getUsername()).isEqualTo("watcher2");

        reflection.perform("details[?]=?", "Sprint", "SPR-002");
        assertThat(bug.getDetails()).containsEntry("Sprint", "SPR-002");

    }

}
