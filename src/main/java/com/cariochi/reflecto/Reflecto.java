package com.cariochi.reflecto;

import com.cariochi.reflecto.fields.NullReflection;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public class Reflecto implements Reflection {

    private final Object instance;

    public static Reflection reflect(Object instance) {
        return instance == null ? new NullReflection() : new Reflecto(instance);
    }

    @Override
    public <V> V getValue() {
        return (V) instance;
    }

    @Override
    public <V> void setValue(V value) {
        throw new UnsupportedOperationException();
    }


}
