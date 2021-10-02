package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.Reflection;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ArrayField implements Reflection {

    private final Object[] target;
    private final Integer index;

    public <V> V getValue() {
        return (V) target[index];
    }

    public <V> void setValue(V value) {
        target[index] = value;
    }

}
