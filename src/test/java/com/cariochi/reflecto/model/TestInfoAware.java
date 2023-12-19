package com.cariochi.reflecto.model;

public interface TestInfoAware {

    default String getTestInfo() {
        return "Hello, World!";
    }

}
