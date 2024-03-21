package com.cariochi.reflecto.constructors;

import com.cariochi.reflecto.base.ReflectoAnnotations;
import com.cariochi.reflecto.base.ReflectoExceptions;
import com.cariochi.reflecto.base.ReflectoModifiers;
import com.cariochi.reflecto.parameters.ReflectoParameters;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Constructor;
import java.util.function.Supplier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@Accessors(fluent = true)
public class ReflectoConstructor {

    @Getter
    @EqualsAndHashCode.Include
    private final Constructor<?> rawConstructor;

    private final Supplier<ReflectoType> declaringTypeSupplier;

    @Getter(lazy = true)
    private final ReflectoType declaringType = declaringTypeSupplier.get();

    @Getter(lazy = true)
    private final ReflectoAnnotations annotations = new ReflectoAnnotations(rawConstructor);

    @Getter(lazy = true)
    private final ReflectoModifiers modifiers = new ReflectoModifiers(rawConstructor.getModifiers());

    @Getter(lazy = true)
    private final ReflectoParameters parameters = new ReflectoParameters(rawConstructor, declaringType());

    @Getter(lazy = true)
    private final ReflectoExceptions exceptions = new ReflectoExceptions(rawConstructor, declaringType());

    public String name() {
        return rawConstructor.getName();
    }

    @SneakyThrows
    public <V> V newInstance(Object... initArgs) {
        final boolean accessible = rawConstructor.isAccessible();
        rawConstructor.setAccessible(true);
        final V result = (V) rawConstructor.newInstance(initArgs);
        rawConstructor.setAccessible(accessible);
        return result;
    }

    public String toGenericString() {
        return rawConstructor.toGenericString();
    }

    public boolean isVarArgs() {
        return rawConstructor.isVarArgs();
    }

    public boolean isSynthetic() {
        return rawConstructor.isSynthetic();
    }


}
