package com.cariochi.reflecto.dto;

import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@With
@NoArgsConstructor
@AllArgsConstructor
public class TestDto {

    private Integer id;

    @TestAnnotation
    private String text;

    private String nullValue;

    private List<String> strings;

    private Instant date;

    private TestDto parent;

    @Builder.Default
    private Map<String, TestDto> dependencies = new HashMap<>();

    @Builder.Default
    private List<TestDto> children = new ArrayList<>();

    public static TestDto dto(int id) {
        return TestDto.builder()
                .id(id)
                .text("Test Object " + id)
                .strings(new ArrayList<>(List.of(String.valueOf(id), String.valueOf(id + 1), String.valueOf(id + 2))))
                .date(Instant.ofEpochSecond(1577836800 + 86400L * id))
                .build();
    }

    @TestAnnotation
    public TestDto withChild(TestDto child) {
        children.add(child);
        return this;
    }

    public TestDto withDependency(String key, TestDto dto) {
        dependencies.put(key, dto);
        return this;
    }

}
