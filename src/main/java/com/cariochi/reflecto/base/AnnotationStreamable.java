package com.cariochi.reflecto.base;

import com.cariochi.reflecto.exceptions.NotFoundException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static com.cariochi.reflecto.utils.MessageUtils.annotationNames;

/**
 * Streamable annotation collection with typed lookup helpers.
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class AnnotationStreamable implements Streamable<Annotation> {

    private final Supplier<List<Annotation>> listSupplier;

    @Getter(lazy = true)
    private final List<Annotation> list = listSupplier.get();

    /**
     * Finds an annotation by annotation type.
     *
     * @param annotationClass annotation class
     * @param <A> annotation type
     * @return matching annotation, if present
     */
    public <A extends Annotation> Optional<A> find(Class<A> annotationClass) {
        return stream()
                .filter(annotationClass::isInstance)
                .map(annotationClass::cast)
                .findFirst();
    }

    /**
     * Returns an annotation by annotation type.
     *
     * @param annotationClass annotation class
     * @param <A> annotation type
     * @return matching annotation
     * @throws NotFoundException when no annotation matches
     */
    public <A extends Annotation> A get(Class<A> annotationClass) {
        return find(annotationClass)
                .orElseThrow(() -> new NotFoundException("Annotation {0} not found. Available annotations: {1}",
                        annotationClass.getName(),
                        annotationNames(stream())));
    }

    /**
     * Checks whether the collection contains an annotation of the supplied type.
     *
     * @param annotationClass annotation class
     * @param <A> annotation type
     * @return {@code true} when present
     */
    public <A extends Annotation> boolean contains(Class<A> annotationClass) {
        return find(annotationClass).isPresent();
    }
}
