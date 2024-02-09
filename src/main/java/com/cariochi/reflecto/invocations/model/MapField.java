package com.cariochi.reflecto.invocations.model;

import com.cariochi.reflecto.Reflecto;
import com.cariochi.reflecto.types.ReflectoType;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MapField implements Reflection {

    private final Map<Object, Object> target;
    private final Object key;
    private final ReflectoType type;

    public <V> V getValue() {
        return (V) target.get(key);
    }

    public <V> void setValue(V value) {
        target.put(key, value);
    }

    @Override
    public ReflectoType type() {
        if (!type.isTypeVariable()) {
            return type;
        }
        for (Object next : target.values()) {
            if (next != null) {
                return Reflecto.reflect(next.getClass());
            }
        }
        return null;
    }

}
