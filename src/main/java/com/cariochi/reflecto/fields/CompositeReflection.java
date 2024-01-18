package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.Invocations;
import com.cariochi.reflecto.Reflection;
import com.cariochi.reflecto.methods.JavaMethod;
import com.cariochi.reflecto.methods.Methods;
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
    public Reflection get(String path, Object... args) {
        final Invocations invocations = Invocations.parse(path, args);
        final List<Reflection> reflectionsList = reflections.stream()
                .flatMap(reflection -> invocations.apply(reflection.getValue()).stream())
                .collect(toList());
        return new CompositeReflection(reflectionsList);
    }

    @Override
    public Fields fields() {
        throw new NotImplementedException("Not supported for composite reflection");
    }

    @Override
    public JavaField field(String name) {
        throw new NotImplementedException("Not supported for composite reflection");
    }

    @Override
    public Methods methods() {
        throw new NotImplementedException("Not supported for composite reflection");
    }

    @Override
    public JavaMethod method(String name, Class<?>... argClasses) {
        throw new NotImplementedException("Not supported for composite reflection");
    }

}
