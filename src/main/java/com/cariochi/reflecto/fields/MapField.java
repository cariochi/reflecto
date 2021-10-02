package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.Reflection;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class MapField implements Reflection {

    private final Map<Object, Object> target;
    private final String key;

    public <V> V getValue() {
        return (V) target.get(key);
    }

    public <V> void setValue(V value) {
        target.put(key, value);
    }

}
