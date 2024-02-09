package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.methods.TargetMethod;
import com.cariochi.reflecto.methods.TargetMethods;
import com.cariochi.reflecto.model.Enclosing;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.reflect;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class EnclosingTest {

    private final Enclosing enclosing = new Enclosing("bla bla");

    @Nested
    class EnclosingFieldsTest {

        @Test
        void should_get_enclosing_fields() {
            final TargetFields fields = reflect(enclosing.first.second).fields().includeEnclosing();
            assertThat(fields.list())
                    .extracting(TargetField::name, TargetField::getValue)
                    .containsExactlyInAnyOrder(
                            tuple("first", enclosing.first),
                            tuple("second", enclosing.first.second),
                            tuple("third", enclosing.first.second.third),
                            tuple("summary", "bla bla"),
                            tuple("deprecatedString", "has been"),
                            tuple("deprecatedInt", -1));
        }

        @Test
        void should_get_not_enclosing_fields() {
            final TargetFields fields = reflect(enclosing.first.second).fields();
            assertThat(fields.list())
                    .extracting(TargetField::name, TargetField::getValue)
                    .containsExactly(tuple("third", enclosing.first.second.third));
        }

        @Test
        void should_get_enclosing_fields_with_annotation() {
            final TargetFields fields = reflect(enclosing.first.second).fields().includeEnclosing();
            assertThat(fields.withAnnotation(Deprecated.class).list())
                    .extracting(TargetField::name, TargetField::getValue)
                    .containsExactlyInAnyOrder(
                            tuple("deprecatedString", "has been"),
                            tuple("deprecatedInt", -1));
        }

        @Test
        void should_get_enclosing_fields_with_type() {
            final TargetFields fields = reflect(enclosing.first.second).fields().includeEnclosing();
            assertThat(fields.withType(String.class).list())
                    .extracting(TargetField::name, TargetField::getValue)
                    .containsExactlyInAnyOrder(
                            tuple("summary", "bla bla"),
                            tuple("deprecatedString", "has been"),
                            tuple("third", "third")
                    );
        }

        @Test
        void should_get_enclosing_class_field_with_type_and_annotation() {
            final TargetFields fields = reflect(enclosing.first.second).fields().includeEnclosing();
            assertThat(fields.withType(String.class).withAnnotation(Deprecated.class).list())
                    .extracting(TargetField::name, TargetField::getValue)
                    .containsExactly(tuple("deprecatedString", "has been"));
        }

    }

    @Nested
    class EnclosingModelsTest {

        @Test
        void should_get_enclosing_methods() {
            final TargetMethods methods = reflect(enclosing.first.second).methods().includeEnclosing();
            assertThat(methods.find("methodFirst()"))
                    .map(TargetMethod::invoke)
                    .contains("first");
            assertThat(methods.find("methodSecond()"))
                    .map(TargetMethod::invoke)
                    .contains("second");
        }

        @Test
        void should_get_not_enclosing_methods() {
            final TargetMethods methods = reflect(enclosing.first.second).methods();
            assertThat(methods.find("methodFirst()")).isEmpty();
            assertThat(methods.find("methodSecond()"))
                    .map(TargetMethod::invoke)
                    .contains("second");
        }

    }

}
