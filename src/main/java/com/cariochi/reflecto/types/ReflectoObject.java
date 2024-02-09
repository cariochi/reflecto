package com.cariochi.reflecto.types;

import com.cariochi.reflecto.invocations.model.Reflection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@Accessors(fluent = true)
public class ReflectoObject implements Reflection {

    @EqualsAndHashCode.Include
    private final Object instance;

    @Getter
    private final ReflectoType type;

    @SuppressWarnings("unchecked")
    @Override
    public <V> V getValue() {
        return (V) instance;
    }

    @Override
    public <V> void setValue(V value) {
        throw new UnsupportedOperationException();
    }

}
