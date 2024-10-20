package com.cariochi.reflecto.base;

import com.cariochi.reflecto.exceptions.NotFoundException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class AnnotationStreamable implements Streamable<Annotation> {

    private final Supplier<List<Annotation>> listSupplier;

    @Getter(lazy = true)
    private final List<Annotation> list = listSupplier.get();

    public <A extends Annotation> Optional<A> find(Class<A> annotationClass) {
        return stream()
                .filter(annotationClass::isInstance)
                .map(annotationClass::cast)
                .findFirst();
    }

    public <A extends Annotation> A get(Class<A> annotationClass) {
        return find(annotationClass)
                .orElseThrow(() -> new NotFoundException("Annotation {0} not found", annotationClass));
    }

    public <A extends Annotation> boolean contains(Class<A> annotationClass) {
        return find(annotationClass).isPresent();
    }
}
