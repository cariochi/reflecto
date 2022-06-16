package com.cariochi.reflecto.model;

import lombok.Getter;

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

    public class NestedClass {

        public SecondNestedClass secondNested = new SecondNestedClass();

        public class SecondNestedClass {

        }
    }
}
