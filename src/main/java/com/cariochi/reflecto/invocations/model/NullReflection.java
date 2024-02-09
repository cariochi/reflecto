package com.cariochi.reflecto.invocations.model;

import com.cariochi.reflecto.types.ReflectoType;

public class NullReflection implements Reflection {

    @Override
    public ReflectoType type() {
        return null;
    }

    @Override
    public <V> V getValue() {
        return null;
    }

    @Override
    public <V> void setValue(V value) {
        throw new NullPointerException();
    }

    @Override
    public Reflection reflect(String expression, Object... args) {
        return this;
    }

}
