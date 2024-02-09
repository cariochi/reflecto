package com.cariochi.reflecto;

import com.cariochi.reflecto.fields.TargetField;
import com.cariochi.reflecto.invocations.model.Reflection;
import com.cariochi.reflecto.model.Bug;
import com.cariochi.reflecto.model.User;
import com.cariochi.reflecto.types.ReflectoType;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.reflect;
import static com.cariochi.reflecto.TestData.bug;
import static com.cariochi.reflecto.TestData.modifiedBug;
import static org.assertj.core.api.Assertions.assertThat;

class ReflectoTest {

    @Test
    void should_get_field_value() {
        final Reflection bug = reflect(bug());
        assertValueEquals(bug.reflect("reporter.id"), 100);
        assertValueEquals(bug.reflect("getReporter().getId()"), 100);
        assertValueEquals(bug.reflect("reporter.username"), "qa");
        assertValueEquals(bug.reflect("getReporter().getUsername()"), "qa");
    }

    @Test
    void should_get_field_of_list() {
        final Reflection bug = reflect(bug());
        assertValueEquals(bug.reflect("watchers[1].username"), "manager");
        assertValueEquals(bug.reflect("getWatchers()[1].username"), "manager");
        assertValueEquals(bug.reflect("watchers.get(?).username", 1), "manager");
        assertValueEquals(bug.reflect("getWatchers().get(?).username", 1), "manager");
        assertValueEquals(bug.reflect("watchers").reflect("[1]").reflect("username"), "manager");
        assertValueEquals(bug.reflect("getWatchers()").reflect("get(?)", 1).reflect("getUsername()"), "manager");
    }

    @Test
    void should_get_field_of_array() {
        final Reflection bug = reflect(bug());
        assertValueEquals(bug.reflect("tags[1]"), "user");
        assertValueEquals(bug.reflect("getTags()[1]"), "user");
    }

    @Test
    void should_get_field_of_map() {
        final Reflection bug = reflect(bug());
        assertValueEquals(bug.reflect("details[Sprint]"), "SPR-001");
        assertValueEquals(bug.reflect("getDetails()[Sprint]"), "SPR-001");
        assertValueEquals(bug.reflect("details.get(?)", "Sprint"), "SPR-001");
        assertValueEquals(bug.reflect("getDetails().get(?)", "Sprint"), "SPR-001");
        assertValueEquals(bug.reflect("details").reflect("[Sprint]"), "SPR-001");
        assertValueEquals(bug.reflect("getDetails()").reflect("get(?)", "Sprint"), "SPR-001");
    }

    @Test
    void should_set_field_value() {
        final Bug bug = bug();
        final Reflection reflection = reflect(bug);
        reflection.perform("summary=?", "Modified bug");
        reflection.perform("watchers[0].id=?", 1001);
        reflection.perform("watchers.get(?).username=?", 0, "java-dev");
        reflection.reflect("watchers.get(?).id", 1).setValue(1002);
        reflection.reflect("watchers.get(?).username", 1).setValue("pm");
        reflection.reflect("tags[0]").setValue("roles");
        reflection.reflect("details[?]", "Sprint").setValue("SPR-002");
        assertThat(bug).isEqualTo(modifiedBug());
    }

    @Test
    void should_set_field_value_with_nulls() {
        final Bug bug = bug();
        final Reflection reflection = reflect(bug);
        reflection.perform("summary=?", null);
        reflection.perform("watchers[0].id=?", null);
        reflection.perform("tags[?]=?", 0, null);
        assertThat(bug)
                .extracting(Bug::getSummary, b -> b.getWatchers().get(0).getId(), b -> b.getTags()[0])
                .containsOnlyNulls();
    }

    @Test
    void should_invoke_method() {
        final Bug bug = bug();
        final Reflection reflection = reflect(bug);
        reflection.perform("setSummary(?)", "Modified bug");
        reflection.perform("getWatchers().get(?).setId(?)", 0, 1001);
        reflection.reflect("getWatchers().get(?)", 0).methods().find("setUsername(?)", String.class).ifPresent(method -> method.invoke("java-dev"));
        reflection.perform("getWatchers().remove(?)", 1);
        reflection.perform("getWatchers().add(?)", new User(1002, "pm"));
        reflection.perform("getTags()[?]=?", 0, "roles");
        reflection.perform("getDetails().remove(?)", "Sprint");
        reflection.perform("getDetails().put(?,?)", "Sprint", "SPR-002");
        assertThat(bug).isEqualTo(modifiedBug());
    }

    @Test
    void should_invoke_method_with_nulls() {
        final Bug bug = bug();
        final Reflection reflection = reflect(bug);
        reflection.perform("setSummary(?)", null);
        reflection.perform("getWatchers().get(?).setId(?)", 0, null);
        reflection.perform("getTags()[?]=?", 0, null);
        assertThat(bug)
                .extracting(Bug::getSummary, b -> b.getWatchers().get(0).getId(), b -> b.getTags()[0])
                .containsOnlyNulls();
    }

    @Test
    void should_get_java_field() {
        final Optional<TargetField> reporter = reflect(bug()).reflect("reporter").fields().find("username");

        assertThat(reporter)
                .map(TargetField::type)
                .map(ReflectoType::actualType)
                .contains(String.class);

        assertThat(reporter)
                .map(TargetField::getValue)
                .contains("qa");
    }

    @Test
    void should_invoke_default_method() {
        final Bug bug = bug();
        final Reflection reflection = reflect(bug);
        final String testInfo = reflection.perform("getTestInfo()");
        assertThat(testInfo).isEqualTo(bug.getTestInfo());
    }

    void assertValueEquals(Reflection reflection, Object expected) {
        assertThat(reflection)
                .extracting(Reflection::getValue)
                .isEqualTo(expected);
    }

}
