package com.cariochi.reflecto.constructors;

import java.lang.reflect.Constructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@Accessors(fluent = true)
public class ConstructorInvocation {

    @Getter
    @EqualsAndHashCode.Include
    private final ReflectoConstructor constructor;

    private final Object[] arguments;

    @SneakyThrows
    public <V> V newInstance() {
        final Constructor<?> rawConstructor = constructor.rawConstructor();
        final boolean accessible = rawConstructor.canAccess(null);
        rawConstructor.setAccessible(true);
        final Object result = rawConstructor.newInstance(arguments);
        rawConstructor.setAccessible(accessible);
        return (V) result;
    }
}
