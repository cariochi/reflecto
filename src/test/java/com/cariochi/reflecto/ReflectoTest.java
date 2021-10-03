package com.cariochi.reflecto;

import com.cariochi.recordo.Read;
import com.cariochi.recordo.RecordoExtension;
import com.cariochi.reflecto.fields.JavaField;
import com.cariochi.reflecto.model.Bug;
import com.cariochi.reflecto.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.cariochi.recordo.assertions.JsonAssertion.assertAsJson;
import static com.cariochi.reflecto.Reflecto.reflect;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(RecordoExtension.class)
class ReflectoTest {

    @Test
    void should_get_field_value(@Read("/bug.json") Bug bug) {
        final Reflection ref = reflect(bug);
        assertValueEquals(ref.get("reporter.id"), 100);
        assertValueEquals(ref.get("getReporter().getId()"), 100);
        assertValueEquals(ref.get("reporter.username"), "qa");
        assertValueEquals(ref.get("getReporter().getUsername()"), "qa");
    }

    @Test
    void should_get_field_of_list(@Read("/bug.json") Bug bug) {
        final Reflection ref = reflect(bug);
        assertValueEquals(ref.get("watchers[1].username"), "manager");
        assertValueEquals(ref.get("getWatchers()[1].username"), "manager");
        assertValueEquals(ref.get("watchers.get(?).username", 1), "manager");
        assertValueEquals(ref.get("getWatchers().get(?).username", 1), "manager");
        assertValueEquals(ref.get("watchers").get("[1]").get("username"), "manager");
        assertValueEquals(ref.get("getWatchers()").get("get(?)", 1).get("getUsername()"), "manager");
    }

    @Test
    void should_get_field_of_array(@Read("/bug.json") Bug bug) {
        final Reflection ref = reflect(bug);
        assertValueEquals(ref.get("tags[1]"), "user");
        assertValueEquals(ref.get("getTags()[1]"), "user");
    }

    @Test
    void should_get_field_of_map(@Read("/bug.json") Bug bug) {
        final Reflection ref = reflect(bug);
        assertValueEquals(ref.get("details[Sprint]"), "RFT-233");
        assertValueEquals(ref.get("getDetails()[Sprint]"), "RFT-233");
        assertValueEquals(ref.get("details.get(?)", "Sprint"), "RFT-233");
        assertValueEquals(ref.get("getDetails().get(?)", "Sprint"), "RFT-233");
        assertValueEquals(ref.get("details").get("[Sprint]"), "RFT-233");
        assertValueEquals(ref.get("getDetails()").get("get(?)", "Sprint"), "RFT-233");
    }

    @Test
    void should_set_field_value(@Read("/bug.json") Bug bug) {
        final Reflection ref = reflect(bug);
        ref.invoke("summary=?", "Modified bug");
        ref.invoke("watchers[0].id=?", 1001);
        ref.invoke("watchers.get(?).username=?", 0, "java-dev");
        ref.get("watchers.get(?).id", 1).setValue(1002);
        ref.get("watchers.get(?).username", 1).setValue("pm");
        ref.get("tags[0]").setValue("roles");
        ref.get("details[Sprint]").setValue("RFT-234");
        assertAsJson(bug).isEqualTo("/modified_bug.json");
    }

    @Test
    void should_invoke_method(@Read("/bug.json") Bug bug) {
        final Reflection ref = reflect(bug);
        ref.invoke("setSummary(?)", "Modified bug");
        ref.invoke("getWatchers().get(?).setId(?)", 0, 1001);
        ref.get("getWatchers().get(?)", 0).methods().get("setUsername(?)", String.class).invoke("java-dev");
        ref.invoke("getWatchers().remove(?)", 1);
        ref.invoke("getWatchers().add(?)", new User(1002, "pm"));
        ref.invoke("tags[0]=?", "roles");
        ref.invoke("getDetails().put(?,?)", "Sprint", "RFT-234");
        assertAsJson(bug).isEqualTo("/modified_bug.json");
    }

    @Test
    void should_get_java_field(@Read("/bug.json") Bug bug) {
        final JavaField loadFactor = reflect(bug).get("details").fields().get("loadFactor");
        assertThat(loadFactor.getType()).isEqualTo(float.class);
        assertThat((float) loadFactor.getValue()).isEqualTo(0.75f);
    }

    void assertValueEquals(Reflection reflection, Object expected) {
        assertThat(reflection)
                .extracting(Reflection::getValue)
                .isEqualTo(expected);
    }

}
