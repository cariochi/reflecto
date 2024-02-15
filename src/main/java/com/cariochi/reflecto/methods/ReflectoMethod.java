package com.cariochi.reflecto.methods;

import com.cariochi.reflecto.base.IsMethod;
import com.cariochi.reflecto.base.ReflectoAnnotations;
import com.cariochi.reflecto.fields.ReflectoField;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import static java.lang.String.format;
import static java.util.Arrays.asList;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@Accessors(fluent = true)
public class ReflectoMethod implements IsMethod {

    @EqualsAndHashCode.Include
    private final Method rawMethod;

    private final ReflectoType declaringType;

    private final Set<ReflectoMethod> superMethods = new HashSet<>();

    @Getter
    private final Declared declared = new Declared();

    @Setter
    private ReflectoField syntheticParent;

    public TargetMethod withTarget(Object target) {
        if (modifiers().isStatic()) {
            return asStatic();
        }
        if (syntheticParent != null) {
            target = syntheticParent.withTarget(target).getValue();
        }
        return new TargetMethod(target, this);
    }

    public TargetMethod asStatic() {
        if (!modifiers().isStatic()) {
            throw new IllegalArgumentException(format("Field %s is not static", name()));
        }
        return new TargetMethod(null, this);
    }

    public void addSuperMethod(ReflectoMethod method) {
        superMethods.add(method);
    }

    @Override
    public ReflectoAnnotations annotations() {
        return new ReflectoAnnotations(this::collectAnnotations);
    }

    private List<Annotation> collectAnnotations() {
        final List<Annotation> annotations = new ArrayList<>();
        annotations.addAll(asList(rawMethod().getDeclaredAnnotations()));
        superMethods().stream()
                .map(m -> m.collectAnnotations())
                .forEach(annotations::addAll);
        return annotations;
    }

    public class Declared {

        @Getter(lazy = true)
        private final ReflectoAnnotations annotations = new ReflectoAnnotations(() ->  asList(rawMethod().getDeclaredAnnotations()));

    }

    @Override
    public String toString() {
        return rawMethod.toString();
    }

}