package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.base.Streamable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class TargetFields implements Streamable<TargetField> {

    private final ReflectoFields fields;

    private final Object target;

    @Getter(lazy = true)
    private final List<TargetField> list = fields.stream().map(field -> field.withTarget(target)).collect(toList());

    public TargetFields includeEnclosing() {
        return new TargetFields(fields.includeEnclosing(), target);
    }

    public Optional<TargetField> find(String name) {
        return fields.find(name)
                .map(f -> f.withTarget(target));
    }

    public TargetFields withType(Type fieldType) {
        return new TargetFields(fields.withType(fieldType), target);
    }

    public TargetFields withAnnotation(Class<? extends Annotation> annotationCls) {
        return new TargetFields(fields.withAnnotation(annotationCls), target);
    }

    public TargetFields withDeclaredAnnotation(Class<? extends Annotation> annotationCls) {
        return new TargetFields(fields.withDeclaredAnnotation(annotationCls), target);
    }

}
