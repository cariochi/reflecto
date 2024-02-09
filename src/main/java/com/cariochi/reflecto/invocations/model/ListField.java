package com.cariochi.reflecto.invocations.model;

import com.cariochi.reflecto.Reflecto;
import com.cariochi.reflecto.types.ReflectoType;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListField implements Reflection {

    private final List<Object> target;
    private final Integer index;
    private final ReflectoType type;

    @Override
    public ReflectoType type() {
        if (!type.isTypeVariable()) {
            return type;
        }
        for (Object next : target) {
            if (next != null) {
                return Reflecto.reflect(next.getClass());
            }
        }
        return null;
    }

    public <V> V getValue() {
        return (V) target.get(index);
    }

    public <V> void setValue(V value) {
        target.set(index, value);
    }

}
