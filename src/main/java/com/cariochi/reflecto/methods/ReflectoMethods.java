package com.cariochi.reflecto.methods;

import com.cariochi.reflecto.base.Streamable;
import com.cariochi.reflecto.fields.ReflectoField;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static com.cariochi.reflecto.utils.MethodsUtils.findMatchingMethod;
import static com.cariochi.reflecto.utils.MethodsUtils.getMethods;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.substringBefore;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class ReflectoMethods implements Streamable<ReflectoMethod> {

    private final Supplier<List<ReflectoMethod>> listSupplier;

    private final ReflectoType declaringType;

    @Getter(lazy = true)
    private final List<ReflectoMethod> list = listSupplier.get();

    public ReflectoMethods(ReflectoType declaringType) {
        this.listSupplier = () -> collectMethods(declaringType, false);
        this.declaringType = declaringType;
    }

    public ReflectoMethods includeEnclosing() {
        return new ReflectoMethods(() -> collectMethods(declaringType, true), declaringType);
    }

    public Optional<ReflectoMethod> find(String name, Class<?>... argClasses) {
        final String methodName = substringBefore(name, "(");
        return findMatchingMethod(list(), methodName, argClasses);
    }

    public ReflectoMethods withAnnotation(Class<? extends Annotation> annotationCls) {
        return new ReflectoMethods(
                () -> stream()
                        .filter(method -> method.annotations().contains(annotationCls))
                        .collect(toList()),
                declaringType
        );
    }

    public ReflectoMethods withDeclaredAnnotation(Class<? extends Annotation> annotationCls) {
        return new ReflectoMethods(
                () -> stream()
                        .filter(method -> method.annotations().declared().contains(annotationCls))
                        .collect(toList()),
                declaringType
        );
    }

    private List<ReflectoMethod> collectMethods(ReflectoType declaringType, boolean includeEnclosing) {
        final List<ReflectoMethod> methods = new ArrayList<>(getMethods(declaringType));
        if (includeEnclosing) {
            Stream.of(declaringType.actualClass().getDeclaredFields())
                    .map(field -> new ReflectoField(field, declaringType))
                    .map(this::getEnclosingMethods)
                    .flatMap(List::stream)
                    .forEach(methods::add);
        }
        return methods;
    }

    private List<ReflectoMethod> getEnclosingMethods(ReflectoField syntheticField) {
        final ArrayList<ReflectoMethod> enclosingMethods = new ArrayList<>();

        syntheticField.type().methods().stream()
                .peek(f -> f.syntheticParent(syntheticField))
                .forEach(enclosingMethods::add);

        Stream.of(syntheticField.type().actualClass().getDeclaredFields())
                .map(field -> new ReflectoField(field, syntheticField.type()))
                .filter(ReflectoField::isSynthetic)
                .peek(field -> field.syntheticParent(syntheticField))
                .map(this::getEnclosingMethods)
                .flatMap(List::stream)
                .forEach(enclosingMethods::add);

        return enclosingMethods;
    }

}
