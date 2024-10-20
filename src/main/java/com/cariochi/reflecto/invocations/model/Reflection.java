package com.cariochi.reflecto.invocations.model;

import com.cariochi.reflecto.fields.TargetFields;
import com.cariochi.reflecto.invocations.Invocations;
import com.cariochi.reflecto.methods.TargetMethods;
import com.cariochi.reflecto.types.ReflectoType;
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

}
