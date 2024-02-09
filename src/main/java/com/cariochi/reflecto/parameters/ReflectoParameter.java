package com.cariochi.reflecto.parameters;

import com.cariochi.reflecto.base.ReflectoAnnotations;
import com.cariochi.reflecto.base.ReflectoModifiers;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Parameter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@Accessors(fluent = true)
public class ReflectoParameter {

    @EqualsAndHashCode.Include
    @Getter
    private final Parameter rawParameter;

    private final ReflectoType declaringType;

    @Getter(lazy = true)
    private final ReflectoAnnotations annotations = new ReflectoAnnotations(rawParameter);

    @Getter(lazy = true)
    private final ReflectoModifiers modifiers = new ReflectoModifiers(rawParameter.getModifiers());

    public boolean isNamePresent() {
        return rawParameter.isNamePresent();
    }

    public String name() {
        return rawParameter.getName();
    }

    public ReflectoType type() {
        return declaringType.reflect(rawParameter.getParameterizedType());
    }

    public boolean isSynthetic() {
        return rawParameter.isSynthetic();
    }

    public boolean isVarArgs() {
        return rawParameter().isVarArgs();
    }

    public boolean isImplicit() {
        return rawParameter().isImplicit();
    }

}
