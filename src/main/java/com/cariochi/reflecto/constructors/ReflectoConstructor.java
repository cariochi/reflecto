package com.cariochi.reflecto.constructors;

import com.cariochi.reflecto.base.ReflectoAnnotations;
import com.cariochi.reflecto.base.ReflectoExceptions;
import com.cariochi.reflecto.base.ReflectoModifiers;
import com.cariochi.reflecto.parameters.ReflectoParameters;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Constructor;
import java.util.List;
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
    private final ReflectoAnnotations annotations = new ReflectoAnnotations(
            () -> List.of(rawConstructor.getAnnotations()),
            () -> List.of(rawConstructor.getDeclaredAnnotations())
    );

    @Getter(lazy = true)
    private final ReflectoModifiers modifiers = new ReflectoModifiers(rawConstructor.getModifiers());

    @Getter(lazy = true)
    private final ReflectoParameters parameters = new ReflectoParameters(rawConstructor, declaringType());

    @Getter(lazy = true)
    private final ReflectoExceptions exceptions = new ReflectoExceptions(rawConstructor, declaringType());

    public String name() {
        return rawConstructor.getName();
    }


    public ConstructorInvocation withArguments(Object... args) {
        return new ConstructorInvocation(this, args);
    }

    @SneakyThrows
    public <V> V newInstance(Object... args) {
        return withArguments(args).newInstance();
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
