package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.Reflection;
import com.cariochi.reflecto.methods.JavaMethod;
import com.cariochi.reflecto.methods.Methods;

public class NullReflection implements Reflection {

    @Override
    public <V> V getValue() {
        return null;
    }

    @Override
    public <V> void setValue(V value) {
        throw new NullPointerException();
    }

    @Override
    public Reflection get(String path, Object... args) {
        return this;
    }

    @Override
    public Fields fields() {
        throw new NullPointerException();
    }

    @Override
    public JavaField field(String name) {
        throw new NullPointerException();
    }

    @Override
    public Methods methods() {
        throw new NullPointerException();
    }

    @Override
    public JavaMethod method(String name, Class<?>... argClasses) {
        throw new NullPointerException();
    }

}
