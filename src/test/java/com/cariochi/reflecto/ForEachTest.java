package com.cariochi.reflecto;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.Test;

import static com.cariochi.reflecto.Reflecto.reflect;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static org.assertj.core.util.Maps.newHashMap;
import static org.assertj.core.util.Sets.newHashSet;

class ForEachTest {

    @Test
    void should_invoke_for_each() {
        final Dto dto = dto(1);

        reflect(dto).perform("array[*].name=?", "TEST");
        assertThat(dto.getArray())
                .extracting(Dto::getName)
                .containsOnly("TEST");

        reflect(dto).perform("set[*].name=?", "TEST");
        assertThat(dto.getSet())
                .extracting(Dto::getName)
                .containsOnly("TEST");

        reflect(dto).perform("list[*].name=?", "TEST");
        assertThat(dto.getList())
                .extracting(Dto::getName)
                .containsOnly("TEST");

        reflect(dto).perform("map[*].name=?", "TEST");
        assertThat(dto.getMap().values())
                .extracting(Dto::getName)
                .containsOnly("TEST");
    }

    @Test
    void should_invoke_for_each_strings() {
        final Dto dto = dto(1);

        reflect(dto).perform("stringArray[*]=?", "TEST");
        assertThat(dto.getStringArray())
                .containsOnly("TEST");

        reflect(dto).perform("stringSet[*]=?", "TEST");
        assertThat(dto.getStringSet())
                .containsOnly("TEST");

        reflect(dto).perform("stringList[*]=?", "TEST");
        assertThat(dto.getStringList())
                .containsOnly("TEST");

        reflect(dto).perform("stringMap[*]=?", "TEST");
        assertThat(dto.getStringMap().values())
                .containsOnly("TEST");
    }

    @Test
    void should_invoke_for_each_complex() {
        final Dto dto = dto(3);

        reflect(dto).perform("set[*].set[*].name=?", "TEST");
        assertThat(dto.getSet())
                .flatExtracting(Dto::getSet)
                .extracting(Dto::getName)
                .containsOnly("TEST");

        reflect(dto).perform("map[*].set[*].name=?", "TEST");
        assertThat(dto.getMap().values())
                .flatExtracting(Dto::getSet)
                .extracting(Dto::getName)
                .containsOnly("TEST");

        reflect(dto).perform("list[*].map[*].name=?", "TEST");
        assertThat(dto.getList())
                .extracting(Dto::getMap)
                .flatExtracting(Map::values)
                .extracting(Dto::getName)
                .containsOnly("TEST");

        reflect(dto).perform("list[?].map[?].stringMap[*]=?", 0, "one", "TEST");
        assertThat(dto.getList().get(0).getMap().get("one").getStringMap().values())
                .containsOnly("TEST");
    }


    @Test
    void should_invoke_for_each_in_list() {
        final List<Dto> dtos = List.of(dto(1), dto(1));

        reflect(dtos).perform("[*].name = ?", "TEST");
        assertThat(dtos)
                .extracting(Dto::getName)
                .containsOnly("TEST");

        reflect(dtos).perform("[*].list[*].name=?", "TEST");
        assertThat(dtos)
                .flatExtracting(Dto::getList)
                .extracting(Dto::getName)
                .containsOnly("TEST");
    }

    @Test
    void should_get_for_each() {
        final Dto dto = dto(3);

        reflect(dto).reflect("set[*]").reflect("set[*]").perform("name=?", "TEST");
        assertThat(reflect(dto).reflect("set[*].set[*].name").<List<String>>getValue())
                .hasSize(4)
                .containsOnly("TEST");
    }

    @Test
    void should_get_null() {
        final Dto dto = dto(1);

        final List<Object> value = reflect(dto).perform("set[*].set[*].name");
        assertThat(value).containsOnlyNulls();
    }

    private Dto dto(int level) {
        if (level == 0) {
            return Dto.builder().name(randomAlphabetic(8)).build();
        }
        return Dto.builder()
                .name(randomAlphabetic(8))
                .array(new Dto[]{dto(level - 1), dto(level - 1)})
                .set(Set.of(dto(level - 1), dto(level - 1)))
                .list(List.of(dto(level - 1), dto(level - 1)))
                .map(Map.of("one", dto(level - 1), "two", dto(level - 1)))
                .stringArray(new String[]{randomAlphabetic(8), randomAlphabetic(8)})
                .stringSet(newHashSet(Set.of(randomAlphabetic(8), randomAlphabetic(8))))
                .stringList(newArrayList(randomAlphabetic(8), randomAlphabetic(8)))
                .stringMap(newHashMap("one", randomAlphabetic(8)))
                .build();
    }


    @Data
    @Builder
    private static class Dto {

        private String name;

        private Dto[] array;
        private Set<Dto> set;
        private List<Dto> list;
        private Map<String, Dto> map;

        private String[] stringArray;
        private Set<String> stringSet;
        private List<String> stringList;
        private Map<String, String> stringMap;

    }

}
