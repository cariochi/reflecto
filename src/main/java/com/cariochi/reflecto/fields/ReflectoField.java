package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.base.IsField;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Field;
import java.util.function.Supplier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static java.lang.String.format;

/**
 * Metadata wrapper around a Java {@link Field}.
 * <p>
 * Use {@link #withTarget(Object)} to bind the field to an object instance and
 * read or write its value.
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@Accessors(fluent = true)
public class ReflectoField implements IsField {

    @EqualsAndHashCode.Include
    private final Field rawField;

    private final Supplier<ReflectoType> declaringTypeSupplier;

    @Getter(lazy = true)
    private final ReflectoType declaringType = declaringTypeSupplier.get();

    /**
     * Binds this field to a target instance.
     * <p>
     * Static fields ignore the supplied target and are bound as static fields.
     *
     * @param target object instance
     * @return target-bound field
     */
    public TargetField withTarget(Object target) {
        if (modifiers().isStatic()) {
            return asStatic();
        }
        return new TargetField(target, this);
    }

    /**
     * Binds this field as a static field.
     *
     * @return static target field
     * @throws IllegalArgumentException when the field is not static
     */
    public TargetField asStatic() {
        if (!modifiers().isStatic()) {
            throw new IllegalArgumentException(format("Field %s is not static", name()));
        }
        return new TargetField(null, this);
    }

    @Override
    public String toString() {
        return rawField.toString();
    }

}
