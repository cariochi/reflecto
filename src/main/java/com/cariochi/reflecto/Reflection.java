package com.cariochi.reflecto;

import com.cariochi.reflecto.fields.JavaField;
import com.cariochi.reflecto.methods.JavaMethod;

public interface Reflection {

    <V> V getValue();

    <V> void setValue(V value);

    default Reflection get(String path, Object... args) {
        return Reflecto.reflect(getValue()).get(path, args);
    }

    default JavaField field(String path, Object... args) {
        return (JavaField) get(path, args);
    }

    default <V> V invoke(String path, Object... args) {
        return get(path, args).getValue();
    }

    default Fields fields() {
        return new Fields(getValue());
    }

    default Methods methods() {
        return new Methods(getValue());
    }

    default JavaMethod method(String name, Class<?>... argClasses) {
        return methods().get(name, argClasses);
    }

}
