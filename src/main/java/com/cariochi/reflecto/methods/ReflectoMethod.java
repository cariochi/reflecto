package com.cariochi.reflecto.methods;

import com.cariochi.reflecto.base.IsMethod;
import com.cariochi.reflecto.base.ReflectoAnnotations;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static com.cariochi.reflecto.utils.TypesUtils.resolveTypeParameters;
import static java.lang.String.format;

/**
 * Metadata wrapper around a Java {@link Method}.
 * <p>
 * Return types, parameters, annotations, exceptions, and inherited method
 * metadata are resolved against the declaring {@link ReflectoType}.
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@Accessors(fluent = true)
public class ReflectoMethod implements IsMethod {

    @EqualsAndHashCode.Include
    private final Method rawMethod;

    private final Supplier<ReflectoType> declaringTypeSupplier;

    @Getter(lazy = true)
    private final ReflectoType declaringType = declaringTypeSupplier.get();

    @Getter(lazy = true)
    private final ReflectoType returnType = determineReturnType();

    private final Set<ReflectoMethod> superMethods = new HashSet<>();

    /**
     * Binds this method to a target instance.
     * <p>
     * Static methods ignore the supplied target and are bound as static methods.
     *
     * @param target object instance
     * @return target-bound method
     */
    public TargetMethod withTarget(Object target) {
        if (modifiers().isStatic()) {
            return asStatic();
        }
        return new TargetMethod(target, this);
    }

    /**
     * Creates a method invocation with pre-bound arguments.
     *
     * @param args invocation arguments
     * @return method invocation
     */
    public ReflectoMethodInvocation withArguments(Object... args) {
        return new ReflectoMethodInvocation(this, args);
    }

    /**
     * Binds this method as a static method.
     *
     * @return static target method
     * @throws IllegalArgumentException when the method is not static
     */
    public TargetMethod asStatic() {
        if (!modifiers().isStatic()) {
            throw new IllegalArgumentException(format("Method %s is not static", name()));
        }
        return new TargetMethod(null, this);
    }

    /**
     * Registers a method from a supertype so inherited annotations can be
     * exposed together with the declared method annotations.
     *
     * @param method super method
     */
    public void addSuperMethod(ReflectoMethod method) {
        superMethods.add(method);
    }

    /**
     * Returns method annotations, including annotations collected from matching
     * super methods.
     */
    @Override
    public ReflectoAnnotations annotations() {
        return new ReflectoAnnotations(this::collectAnnotations, () -> List.of(rawMethod().getDeclaredAnnotations()));
    }

    private List<Annotation> collectAnnotations() {
        final List<Annotation> annotations = new ArrayList<>(List.of(rawMethod().getDeclaredAnnotations()));
        superMethods().stream()
                .map(ReflectoMethod::collectAnnotations)
                .forEach(annotations::addAll);
        return annotations;
    }

    @Override
    public String toString() {
        return rawMethod.toString();
    }

    private ReflectoType determineReturnType() {
        final Type type = resolveTypeParameters(rawMethod.getGenericReturnType(), rawMethod.getTypeParameters());
        return declaringType().reflect(type);
    }

}
