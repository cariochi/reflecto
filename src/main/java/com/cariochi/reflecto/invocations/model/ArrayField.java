package com.cariochi.reflecto.invocations.model;

import com.cariochi.reflecto.Reflecto;
import com.cariochi.reflecto.types.ReflectoType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ArrayField implements Reflection {

    private final Object[] target;
    private final Integer index;
    private final ReflectoType type;

    public <V> V getValue() {
        return (V) target[index];
    }

    public <V> void setValue(V value) {
        target[index] = value;
    }

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

}
