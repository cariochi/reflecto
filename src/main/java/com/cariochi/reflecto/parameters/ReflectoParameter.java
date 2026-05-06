package com.cariochi.reflecto.parameters;

import com.cariochi.reflecto.base.ReflectoAnnotations;
import com.cariochi.reflecto.base.ReflectoModifiers;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Supplier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static com.cariochi.reflecto.utils.TypesUtils.resolveTypeParameters;

/**
 * Metadata wrapper around a Java {@link Parameter}.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@Accessors(fluent = true)
public class ReflectoParameter {

    @EqualsAndHashCode.Include
    @Getter
    private final Parameter rawParameter;

    private final Supplier<ReflectoType> declaringTypeSupplier;

    @Getter(lazy = true)
    private final ReflectoType declaringType = declaringTypeSupplier.get();

    @Getter(lazy = true)
    private final ReflectoAnnotations annotations = new ReflectoAnnotations(
            () -> List.of(rawParameter.getAnnotations()),
            () -> List.of(rawParameter.getDeclaredAnnotations())
    );

    @Getter(lazy = true)
    private final ReflectoModifiers modifiers = new ReflectoModifiers(rawParameter.getModifiers());

    @Getter(lazy = true)
    private final ReflectoType type = determineType();

    /**
     * Checks whether the parameter name is present in bytecode.
     *
     * @return {@code true} when the parameter name is present in bytecode
     */
    public boolean isNamePresent() {
        return rawParameter.isNamePresent();
    }

    /**
     * @return parameter name
     */
    public String name() {
        return rawParameter.getName();
    }

    /**
     * @return {@code true} when the parameter is synthetic
     */
    public boolean isSynthetic() {
        return rawParameter.isSynthetic();
    }

    /**
     * @return {@code true} when the parameter is a varargs parameter
     */
    public boolean isVarArgs() {
        return rawParameter().isVarArgs();
    }

    /**
     * @return {@code true} when the parameter is implicitly declared
     */
    public boolean isImplicit() {
        return rawParameter().isImplicit();
    }

    private ReflectoType determineType() {
        final Type type = resolveTypeParameters(rawParameter.getParameterizedType(), rawParameter().getDeclaringExecutable().getTypeParameters());
        return declaringType().reflect(type);
    }

}
