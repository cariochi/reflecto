package com.cariochi.reflecto.fields;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class TargetFields extends FieldsStreamable<TargetField> {

    @Getter
    private final FieldsStreamable<TargetField> declared;

    public TargetFields(ReflectoFields fields, Object target) {
        super(() -> fields.stream().map(field -> field.withTarget(target)).toList());
        declared = new FieldsStreamable<>(() -> fields.declared().stream().map(field -> field.withTarget(target)).toList());
    }
}
