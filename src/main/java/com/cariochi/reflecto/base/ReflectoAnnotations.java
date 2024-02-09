package com.cariochi.reflecto.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static java.util.Arrays.asList;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class ReflectoAnnotations implements Streamable<Annotation> {

    private final Function<Boolean, List<Annotation>> annotationSupplier;

    private final boolean declared;

    @Getter(lazy = true)
    private final List<Annotation> list = annotationSupplier.apply(declared);

    public ReflectoAnnotations(AnnotatedElement element) {
        this(
                declared -> declared ? asList(element.getDeclaredAnnotations()) : asList(element.getAnnotations()),
                false
        );
    }

    public ReflectoAnnotations declared() {
        return new ReflectoAnnotations(annotationSupplier, true);
    }

    public <A extends Annotation> Optional<A> find(Class<A> annotationClass) {
        return stream()
                .filter(annotationClass::isInstance)
                .map(annotationClass::cast)
                .findFirst();
    }

    public <A extends Annotation> boolean contains(Class<A> annotationClass) {
        return find(annotationClass).isPresent();
    }

}
