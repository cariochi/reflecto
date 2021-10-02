package com.cariochi.reflecto;

import com.cariochi.reflecto.fields.ArrayField;
import com.cariochi.reflecto.fields.JavaField;
import com.cariochi.reflecto.fields.ListField;
import com.cariochi.reflecto.fields.MapField;
import com.cariochi.reflecto.methods.Invocation;
import com.cariochi.reflecto.methods.Invocations;
import com.cariochi.reflecto.methods.JavaMethod;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.ClassUtils.toClass;
import static org.apache.commons.lang3.StringUtils.substringBetween;

public class Reflecto implements Reflection {

    private Object instance;

    public static Reflection reflect(Object instance) {
        return new Reflecto(instance);
    }

    private Reflecto(Object instance) {
        this.instance = instance;
    }

    @Override
    public <V> V getValue() {
        return (V) instance;
    }

    @Override
    public <V> void setValue(V value) {
        this.instance = value;
    }

    public Reflection get(String path, Object... args) {
        return get(Invocations.parse(path, args));
    }

    public <V> V invoke(String path, Object... args) {
        return get(path, args).getValue();
    }

    private Reflection get(List<Invocation> invocations) {
        final Iterator<Invocation> iterator = invocations.iterator();
        Reflection reflection = null;
        while (iterator.hasNext()) {
            final Invocation invocation = iterator.next();
            final Reflecto reflecto = reflection == null ? this : new Reflecto(reflection.getValue());
            reflection = invocation.isMethod()
                    ? reflecto.getMethod(invocation).invoke(invocation.args())
                    : reflecto.getField(invocation);
        }
        return reflection;
    }

    private Reflection getField(Invocation invocation) {
        if (invocation.name().startsWith("[")) {
            final String key = substringBetween(invocation.name(), "[", "]");
            if (instance.getClass().isArray()) {
                return new ArrayField((Object[]) instance, parseInt(key));
            } else if (instance instanceof List) {
                return new ListField((List<Object>) instance, parseInt(key));
            } else if (instance instanceof Map) {
                return new MapField((Map<Object, Object>) instance, key);
            } else {
                throw new IllegalArgumentException("Cannot parse field path");
            }
        } else {
            return new JavaField(instance, invocation.name());
        }
    }

    private JavaMethod getMethod(Invocation invocation) {
        return new JavaMethod(instance, invocation.name(), toClass(invocation.args()));
    }

}
