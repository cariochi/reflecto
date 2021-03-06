package com.cariochi.reflecto;

import com.cariochi.reflecto.fields.ArrayField;
import com.cariochi.reflecto.fields.JavaField;
import com.cariochi.reflecto.fields.ListField;
import com.cariochi.reflecto.fields.MapField;
import com.cariochi.reflecto.methods.JavaMethod;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static com.cariochi.reflecto.Reflecto.reflect;
import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.ClassUtils.toClass;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.substringBetween;

public class Invocation {

    private final String name;
    private final Object[] args;
    private final boolean isMethod;

    public Invocation(String name, List<Object> args) {
        this.isMethod = name.contains("(");
        this.name = substringBefore(name, "(");
        this.args = args.toArray();
    }

    public Reflection apply(Object instance) {
        if (isMethod) {
            return reflect(getMethod(instance).invoke(args));
        } else {
            Queue<Object> argsQueue = new LinkedList<>(List.of(args));
            final Reflection field = getField(instance, argsQueue);
            if (!argsQueue.isEmpty()) {
                field.setValue(argsQueue.poll());
            }
            return field;
        }
    }

    private JavaMethod getMethod(Object instance) {
        return new JavaMethod(instance, name, toClass(args));
    }

    private Reflection getField(Object instance, Queue<Object> argsQueue) {
        if (name.startsWith("[")) {
            Object key = substringBetween(name, "[", "]");
            if ("?".equals(key)) {
                key = argsQueue.poll();
            }
            if (instance.getClass().isArray()) {
                return new ArrayField((Object[]) instance, parseInt(key.toString()));
            } else if (instance instanceof List) {
                return new ListField((List<Object>) instance, parseInt(key.toString()));
            } else if (instance instanceof Map) {
                return new MapField((Map<Object, Object>) instance, key);
            } else {
                throw new IllegalArgumentException("Cannot parse field path");
            }
        } else {
            return new JavaField(instance, name);
        }
    }

}
