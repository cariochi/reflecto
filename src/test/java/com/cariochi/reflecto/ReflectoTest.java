package com.cariochi.reflecto;

import com.cariochi.reflecto.fields.JavaField;
import com.cariochi.reflecto.model.Bug;
import com.cariochi.reflecto.model.User;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.reflect;
import static com.cariochi.reflecto.TestData.bug;
import static com.cariochi.reflecto.TestData.modifiedBug;
import static org.assertj.core.api.Assertions.assertThat;

class ReflectoTest {

    @Test
    void should_get_field_value() {
        final Reflection ref = reflect(bug());
        assertValueEquals(ref.get("reporter.id"), 100);
        assertValueEquals(ref.get("getReporter().getId()"), 100);
        assertValueEquals(ref.get("reporter.username"), "qa");
        assertValueEquals(ref.get("getReporter().getUsername()"), "qa");
    }

    @Test
    void should_get_field_of_list() {
        final Reflection ref = reflect(bug());
        assertValueEquals(ref.get("watchers[1].username"), "manager");
        assertValueEquals(ref.get("getWatchers()[1].username"), "manager");
        assertValueEquals(ref.get("watchers.get(?).username", 1), "manager");
        assertValueEquals(ref.get("getWatchers().get(?).username", 1), "manager");
        assertValueEquals(ref.get("watchers").get("[1]").get("username"), "manager");
        assertValueEquals(ref.get("getWatchers()").get("get(?)", 1).get("getUsername()"), "manager");
    }

    @Test
    void should_get_field_of_array() {
        final Reflection ref = reflect(bug());
        assertValueEquals(ref.get("tags[1]"), "user");
        assertValueEquals(ref.get("getTags()[1]"), "user");
    }

    @Test
    void should_get_field_of_map() {
        final Reflection ref = reflect(bug());
        assertValueEquals(ref.get("details[Sprint]"), "SPR-001");
        assertValueEquals(ref.get("getDetails()[Sprint]"), "SPR-001");
        assertValueEquals(ref.get("details.get(?)", "Sprint"), "SPR-001");
        assertValueEquals(ref.get("getDetails().get(?)", "Sprint"), "SPR-001");
        assertValueEquals(ref.get("details").get("[Sprint]"), "SPR-001");
        assertValueEquals(ref.get("getDetails()").get("get(?)", "Sprint"), "SPR-001");
    }

    @Test
    void should_set_field_value() {
        final Bug bug = bug();
        final Reflection ref = reflect(bug);
        ref.invoke("summary=?", "Modified bug");
        ref.invoke("watchers[0].id=?", 1001);
        ref.invoke("watchers.get(?).username=?", 0, "java-dev");
        ref.get("watchers.get(?).id", 1).setValue(1002);
        ref.get("watchers.get(?).username", 1).setValue("pm");
        ref.get("tags[0]").setValue("roles");
        ref.get("details[?]", "Sprint").setValue("SPR-002");
        assertThat(bug).isEqualTo(modifiedBug());
    }

    @Test
    void should_invoke_method() {
        final Bug bug = bug();
        final Reflection ref = reflect(bug);
        ref.invoke("setSummary(?)", "Modified bug");
        ref.invoke("getWatchers().get(?).setId(?)", 0, 1001);
        ref.get("getWatchers().get(?)", 0).methods().method("setUsername(?)", String.class).invoke("java-dev");
        ref.invoke("getWatchers().remove(?)", 1);
        ref.invoke("getWatchers().add(?)", new User(1002, "pm"));
        ref.invoke("tags[?]=?", 0, "roles");
        ref.invoke("getDetails().remove(?)", "Sprint");
        ref.invoke("getDetails().put(?,?)", "Sprint", "SPR-002");
        assertThat(bug).isEqualTo(modifiedBug());
    }

    @Test
    void should_get_java_field() {
        final JavaField reporter = reflect(bug()).get("reporter").fields().get("username");
        assertThat(reporter.getType()).isEqualTo(String.class);
        assertThat((String) reporter.getValue()).isEqualTo("qa");
    }

    @Test
    void should_invoke_default_method() {
        final Bug bug = bug();
        final Reflection ref = reflect(bug);
        final String testInfo = ref.invoke("getTestInfo()");
        assertThat(testInfo).isEqualTo(bug.getTestInfo());
    }

    void assertValueEquals(Reflection reflection, Object expected) {
        assertThat(reflection)
                .extracting(Reflection::getValue)
                .isEqualTo(expected);
    }

}
