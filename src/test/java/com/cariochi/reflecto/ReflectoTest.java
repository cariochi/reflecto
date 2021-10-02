package com.cariochi.reflecto;

import com.cariochi.reflecto.dto.TestDto;
import com.cariochi.reflecto.fields.JavaField;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.cariochi.reflecto.dto.TestDto.dto;
import static org.assertj.core.api.Assertions.assertThat;

class ReflectoTest {

    @Test
    void should_get_field_of_field_value() {
        final TestDto testDto = dto(1).withParent(dto(2));
        final Integer id = Reflecto.reflect(testDto).get("parent").get("id").getValue();
        assertThat(id).isEqualTo(2);
    }

    @Test
    void should_get_fields_of_field_value() {
        final TestDto testDto = dto(1).withParent(dto(2));
        final Reflection reflection = Reflecto.reflect(testDto).get("parent");
        final Integer id = reflection.get("id").getValue();
        assertThat(id).isEqualTo(2);
    }

    @Test
    void should_get_field_of_collection() {
        final TestDto testDto = dto(1).withChild(dto(2));
        final List<TestDto> children = Reflecto.reflect(testDto).get("children").getValue();
        final Integer id = Reflecto.reflect(children.get(0)).get("id").getValue();
        assertThat(id).isEqualTo(2);
    }

    @Test
    void should_get_nested_field() {
        final TestDto testDto = dto(1).withParent(dto(2).withParent(dto(3)));
        final Integer id = Reflecto.reflect(testDto).get("parent.parent.id").getValue();
        assertThat(id).isEqualTo(3);
    }

    @Test
    void should_get_nested_field_2() {
        final TestDto testDto = dto(1).withParent(dto(2).withParent(dto(3)));
        final Reflection parent = Reflecto.reflect(testDto).get("parent");
        final Integer id = parent.get("parent.id").getValue();
        assertThat(id).isEqualTo(3);
    }

    @Test
    void should_get_nested_collection_field() {
        final TestDto testDto = dto(1).withChild(dto(2)).withChild(dto(3));

        final Reflection reflection = Reflecto.reflect(testDto);

        final int id = reflection.get("children[0].id").getValue();
        assertThat(id).isEqualTo(2);

        final Reflection children = reflection.get("children");
        final TestDto value = children.get("[0]").getValue();
        assertThat(value.getId()).isEqualTo(2);
    }

    @Test
    void should_get_nested_map_field() {
        final TestDto testDto = dto(1).withDependency("test", dto(2));
        final Reflection reflection = Reflecto.reflect(testDto);
        final Integer id = reflection.get("dependencies[test].id").getValue();
        assertThat(id).isEqualTo(2);

        final Reflection dependencies = reflection.get("dependencies");
        final TestDto value = dependencies.get("[test]").getValue();
        assertThat(value.getId()).isEqualTo(2);
    }

    @Test
    void should_invoke_method() {
        final TestDto dto = dto(1).withDependency("test", dto(2));

        final JavaField loadFactor = Reflecto.reflect(dto).field("dependencies.loadFactor");
        assertThat(loadFactor.getType()).isEqualTo(float.class);
        assertThat((float) loadFactor.getValue()).isEqualTo(0.75f);

        final int id = Reflecto.reflect(dto).get("getDependencies()[test].id").getValue();
        assertThat(id).isEqualTo(2);

        final int id2 = Reflecto.reflect(dto).get("getDependencies()[test].getId()").getValue();
        assertThat(id2).isEqualTo(2);

        final int id3 = Reflecto.reflect(dto).get("getDependencies()").get("get(?)", "test").get("getId()").getValue();
        assertThat(id3).isEqualTo(2);

        Reflecto.reflect(dto).get("getDependencies().get(?).text", "test").setValue("100");
        assertThat(dto.getDependencies().get("test").getText()).isEqualTo("100");

        Reflecto.reflect(dto).get("getDependencies().get(?)", "test").method("setText(?)", String.class).invoke("200");
        assertThat(dto.getDependencies().get("test").getText()).isEqualTo("200");

        Reflecto.reflect(dto).invoke("getDependencies().get(?).setText(?)", "test", "300");
        assertThat(dto.getDependencies().get("test").getText()).isEqualTo("300");

        Reflecto.reflect(dto).invoke("getDependencies().put(?,?)", "new", dto(3));
        assertThat(dto.getDependencies().get("new").getId()).isEqualTo(3);

        Reflecto.reflect(dto).get("getDependencies()").get("[test]").invoke("setText(?)", "400");
        assertThat(dto.getDependencies().get("test").getText()).isEqualTo("400");

        Reflecto.reflect(dto).get("strings").invoke("add(?,?)", 0, "Hello!");
        assertThat(dto.getStrings().get(0)).isEqualTo("Hello!");

        final boolean res = Reflecto.reflect(dto)
                .get("strings")
                .invoke("add(?)", "Hello!!!");
        assertThat(res).isTrue();
    }

}
