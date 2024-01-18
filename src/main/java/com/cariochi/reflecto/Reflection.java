package com.cariochi.reflecto;

import com.cariochi.reflecto.fields.CompositeReflection;
import com.cariochi.reflecto.fields.Fields;
import com.cariochi.reflecto.fields.JavaField;
import com.cariochi.reflecto.methods.JavaMethod;
import com.cariochi.reflecto.methods.Methods;
import java.util.List;

public interface Reflection {

    <V> V getValue();

    <V> void setValue(V value);

    default Reflection get(String path, Object... args) {
        final List<Reflection> reflections = Invocations.parse(path, args).apply(getValue());
        return path.contains("[*]") ? new CompositeReflection(reflections) : reflections.get(0);
    }

    default <V> V invoke(String path, Object... args) {
        return get(path, args).getValue();
    }

    default Fields fields() {
        return new Fields(getValue(), false);
    }

    default JavaField field(String name) {
        return fields().get(name);
    }

    default Methods methods() {
        return new Methods(getValue());
    }

    default JavaMethod method(String name, Class<?>... argClasses) {
        return new Methods(getValue()).method(name, argClasses);
    }

}
