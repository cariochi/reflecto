package com.cariochi.reflecto.methods;

import com.cariochi.reflecto.base.Streamable;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class TargetMethods implements Streamable<TargetMethod> {

    private final ReflectoMethods methods;

    private final Object target;

    @Getter(lazy = true)
    private final List<TargetMethod> list = methods.stream().map(m -> m.withTarget(target)).collect(toList());

    public TargetMethods includeEnclosing() {
        return new TargetMethods(methods.includeEnclosing(), target);
    }

    public Optional<TargetMethod> find(String name, Class<?>... argClasses) {
        return methods.find(name, argClasses)
                .map(m -> m.withTarget(target));
    }

    public TargetMethods withAnnotation(Class<? extends Annotation> annotationCls) {
        return new TargetMethods(methods.withAnnotation(annotationCls), target);
    }

    public TargetMethods withDeclaredAnnotation(Class<? extends Annotation> annotationCls) {
        return new TargetMethods(methods.withDeclaredAnnotation(annotationCls), target);
    }

}
