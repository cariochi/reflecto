package com.cariochi.reflecto.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Getter
public class Enclosing {

    public Enclosing(String summary) {
        this.summary = summary;
    }

    private String summary;

    @Deprecated
    private String deprecatedString = "has been";

    @Deprecated
    private int deprecatedInt = -1;

    public NestedClass nested = new NestedClass();

    public class NestedClass {}
}
