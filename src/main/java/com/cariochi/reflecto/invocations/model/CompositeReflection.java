package com.cariochi.reflecto.invocations.model;

import com.cariochi.reflecto.fields.TargetFields;
import com.cariochi.reflecto.invocations.Invocations;
import com.cariochi.reflecto.methods.TargetMethods;
import com.cariochi.reflecto.types.ReflectoType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class CompositeReflection implements Reflection {

    private final List<? extends Reflection> reflections;

    @Override
    public <V> V getValue() {
        return (V) reflections.stream()
                .map(Reflection::getValue)
                .collect(toList());
    }

    @Override
    public <V> void setValue(V value) {
        reflections.forEach(r -> r.setValue(value));
    }

    @Override
    public Reflection reflect(String expression, Object... args) {
        final Invocations invocations = Invocations.parse(expression, args);
        final List<Reflection> reflectionsList = reflections.stream()
                .flatMap(reflection -> invocations.apply(reflection.getValue(), reflection.type()).stream())
                .collect(toList());
        return new CompositeReflection(reflectionsList);
    }

    @Override
    public ReflectoType type() {
        throw new NotImplementedException("Not supported for composite reflection");
    }

    @Override
    public TargetFields fields() {
        throw new NotImplementedException("Not supported for composite reflection");
    }

    @Override
    public TargetMethods methods() {
        throw new NotImplementedException("Not supported for composite reflection");
    }

}
