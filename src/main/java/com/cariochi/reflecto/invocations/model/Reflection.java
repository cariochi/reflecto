package com.cariochi.reflecto.invocations.model;

import com.cariochi.reflecto.fields.TargetFields;
import com.cariochi.reflecto.invocations.Invocations;
import com.cariochi.reflecto.methods.TargetMethods;
import com.cariochi.reflecto.types.ReflectoType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

public interface Reflection {

    ReflectoType type();

    <V> V getValue();

    <V> void setValue(V value);

    default Reflection reflect(String expression, Object... args) {
        final List<Reflection> reflections = Invocations.parse(expression, args).apply(getValue(), type());
        return expression.contains("[*]") ? new CompositeReflection(reflections) : reflections.get(0);
    }

    default <V> V perform(String expression, Object... args) {
        return reflect(expression, args).getValue();
    }

    default TargetFields fields() {
        return new TargetFields(type().fields(), getValue());
    }

    default TargetMethods methods() {
        return new TargetMethods(type().methods(), getValue());
    }

    default Declared declared() {
        return new Declared(this);
    }

    @RequiredArgsConstructor
    @Accessors(fluent = true)
    class Declared {

        private final Reflection reflection;

        @Getter(lazy = true)
        private final TargetFields fields = new TargetFields(reflection.type().declared().fields(), reflection.getValue());

        @Getter(lazy = true)
        private final TargetMethods methods = new TargetMethods(reflection.type().declared().methods(), reflection.getValue());

    }

}
