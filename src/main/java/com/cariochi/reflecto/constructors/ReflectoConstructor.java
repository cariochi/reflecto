package com.cariochi.reflecto.constructors;

import com.cariochi.reflecto.base.ReflectoAnnotations;
import com.cariochi.reflecto.base.ReflectoExceptionTypes;
import com.cariochi.reflecto.base.ReflectoModifiers;
import com.cariochi.reflecto.parameters.ReflectoParameters;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Constructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@Accessors(fluent = true)
public class ReflectoConstructor {

    @EqualsAndHashCode.Include
    private final Constructor<?> constructor;

    @Getter
    private final ReflectoType declaringType;

    @Getter(lazy = true)
    private final ReflectoAnnotations annotations = new ReflectoAnnotations(constructor);

    @Getter(lazy = true)
    private final ReflectoModifiers modifiers = new ReflectoModifiers(constructor.getModifiers());

    @Getter(lazy = true)
    private final ReflectoParameters parameters = new ReflectoParameters(constructor, declaringType);

    @Getter(lazy = true)
    private final ReflectoExceptionTypes exceptions = new ReflectoExceptionTypes(constructor, declaringType);


    public Constructor<?> asConstructor() {
        return constructor;
    }

    public String name() {
        return constructor.getName();
    }

    @SneakyThrows
    public <V> V newInstance(Object... initargs) {
        final boolean accessible = constructor.isAccessible();
        constructor.setAccessible(true);
        final V result = (V) constructor.newInstance(initargs);
        constructor.setAccessible(accessible);
        return result;
    }

    public String toGenericString() {
        return constructor.toGenericString();
    }

    public boolean isVarArgs() {
        return constructor.isVarArgs();
    }

    public boolean isSynthetic() {
        return constructor.isSynthetic();
    }


}
