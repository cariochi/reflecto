package com.cariochi.reflecto;

import com.cariochi.reflecto.fields.Fields;
import com.cariochi.reflecto.methods.Methods;

public interface Reflection {

    <V> V getValue();

    <V> void setValue(V value);

    default Reflection get(String path, Object... args) {
        return Invocations.parse(path, args).apply(getValue());
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

}
