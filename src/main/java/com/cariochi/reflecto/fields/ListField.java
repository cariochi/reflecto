package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.Reflection;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListField implements Reflection {

    private final List<Object> target;
    private final Integer index;

    public <V> V getValue() {
        return (V) target.get(index);
    }

    public <V> void setValue(V value) {
        target.set(index, value);
    }

}
