package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.base.IsField;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Field;
import java.util.function.Supplier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import static java.lang.String.format;

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

    @Setter
    private ReflectoField syntheticParent;

    public TargetField withTarget(Object target) {
        if (modifiers().isStatic()) {
            return asStatic();
        }
        if (syntheticParent != null) {
            target = syntheticParent.withTarget(target).getValue();
        }
        return new TargetField(target, this);
    }

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
