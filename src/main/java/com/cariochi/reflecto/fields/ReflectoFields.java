package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.base.Streamable;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static java.util.stream.Collectors.toList;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class ReflectoFields implements Streamable<ReflectoField> {

    private final Supplier<List<ReflectoField>> listSupplier;

    private final ReflectoType declaringType;

    @Getter(lazy = true)
    private final List<ReflectoField> list = listSupplier.get();

    public ReflectoFields(ReflectoType declaringType) {
        this.listSupplier = () -> collectFields(declaringType, false);
        this.declaringType = declaringType;
    }

    public ReflectoFields includeEnclosing() {
        return new ReflectoFields(() -> collectFields(declaringType, true), declaringType);
    }

    public Optional<ReflectoField> find(String name) {
        return stream()
                .filter(field -> name.equals(field.name()))
                .findFirst();
    }

    public ReflectoFields withType(Type fieldType) {
        return new ReflectoFields(
                () -> stream().filter(field -> fieldType.equals(field.type().actualType())).collect(toList()),
                declaringType
        );
    }

    public ReflectoFields withAnnotation(Class<? extends Annotation> annotationCls) {
        return new ReflectoFields(
                () -> stream().filter(field -> field.annotations().contains(annotationCls)).collect(toList()),
                declaringType
        );
    }

    public ReflectoFields withDeclaredAnnotation(Class<? extends Annotation> annotationCls) {
        return new ReflectoFields(
                () -> stream().filter(field -> field.annotations().declared().contains(annotationCls)).collect(toList()),
                declaringType
        );
    }

    private List<ReflectoField> collectFields(ReflectoType declaringType, boolean includeEnclosing) {
        final List<ReflectoField> allFields = new ArrayList<>();
        ReflectoType current = declaringType;
        while (current != null) {
            for (Field field : current.actualClass().getDeclaredFields()) {
                final ReflectoField reflectoField = new ReflectoField(field, current);
                if (reflectoField.isSynthetic()) {
                    if (includeEnclosing) {
                        allFields.addAll(getEnclosingFields(reflectoField));
                    }
                } else {
                    allFields.add(reflectoField);
                }
            }
            current = current.superType();
        }
        return allFields;
    }

    private List<ReflectoField> getEnclosingFields(ReflectoField syntheticField) {

        final ArrayList<ReflectoField> enclosingFields = new ArrayList<>();

        final List<ReflectoField> fields = Stream.of(syntheticField.type().actualClass().getDeclaredFields())
                .map(field -> new ReflectoField(field, syntheticField.type()))
                .peek(field -> field.syntheticParent(syntheticField))
                .collect(toList());

        fields.stream()
                .filter(field -> !field.isSynthetic())
                .forEach(enclosingFields::add);

        fields.stream()
                .filter(ReflectoField::isSynthetic)
                .map(this::getEnclosingFields)
                .flatMap(List::stream)
                .forEach(enclosingFields::add);

        return enclosingFields;
    }


}
