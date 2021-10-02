package com.cariochi.reflecto.methods;

import com.cariochi.reflecto.Reflection;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InvocationResult implements Reflection {

    private final Object value;

    @Override
    public <V> V getValue() {
        return (V) value;
    }

    @Override
    public <V> void setValue(V value) {
        throw new UnsupportedOperationException();
    }

}
