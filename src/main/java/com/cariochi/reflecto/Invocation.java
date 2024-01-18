package com.cariochi.reflecto;

import com.cariochi.reflecto.fields.ArrayField;
import com.cariochi.reflecto.fields.JavaField;
import com.cariochi.reflecto.fields.ListField;
import com.cariochi.reflecto.fields.MapField;
import com.cariochi.reflecto.fields.NullReflection;
import com.cariochi.reflecto.methods.JavaMethod;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static com.cariochi.reflecto.Reflecto.reflect;
import static com.cariochi.reflecto.utils.CollectionUtils.queueOf;
import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;
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

    public List<Reflection> apply(Object instance) {

        if (instance == null) {
            return List.of(new NullReflection());
        }

        if (isMethod) {
            return List.of(reflect(getMethod(instance).invoke(args)));
        }

        if (name.equals("[*]")) {
            return applyComposite(instance);
        } else {
            Queue<Object> argsQueue = queueOf(args);
            final Reflection field = getField(instance, argsQueue);
            if (!argsQueue.isEmpty()) {
                field.setValue(argsQueue.poll());
            }
            return List.of(field);
        }
    }

    private List<Reflection> applyComposite(Object instance) {

        if (instance.getClass().isArray()) {
            final Object[] array = (Object[]) instance;
            return IntStream.range(0, array.length)
                    .mapToObj(i -> new ArrayField(array, i))
                    .peek(field -> {
                        if (args.length != 0) {
                            field.setValue(args[0]);
                        }
                    })
                    .collect(toList());
        }

        if (instance instanceof Map) {
            final Map<Object, Object> map = (Map<Object, Object>) instance;
            return map.keySet().stream()
                    .map(o -> new MapField(map, o))
                    .peek(field -> {
                        if (args.length != 0) {
                            field.setValue(args[0]);
                        }
                    })
                    .collect(toList());
        }

        if (instance instanceof List) {
            final List<Object> list = (List<Object>) instance;
            return IntStream.range(0, list.size())
                    .mapToObj(i -> new ListField(list, i))
                    .peek(field -> {
                        if (args.length != 0) {
                            field.setValue(args[0]);
                        }
                    })
                    .collect(toList());
        }

        if (instance instanceof Set) {
            final Set<Object> set = (Set<Object>) instance;
            if (args.length != 0) {
                final int size = set.size();
                set.clear();
                return IntStream.range(0, size)
                        .peek(i -> set.add(args[0]))
                        .mapToObj(i -> reflect(args[0]))
                        .collect(toList());
            } else {
                return set.stream()
                        .map(Reflecto::reflect)
                        .collect(toList());
            }
        }

        if (instance instanceof Iterable) {
            if (args.length != 0) {
                throw new IllegalArgumentException("Cannot modify object of " + instance.getClass());
            } else {
                final Iterable<?> iterable = (Iterable<?>) instance;
                return StreamSupport.stream(iterable.spliterator(), false)
                        .map(Reflecto::reflect)
                        .collect(toList());
            }
        }

        throw new IllegalArgumentException("Cannot iterate " + instance.getClass());
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
                throw new IllegalArgumentException("Cannot access by index of " + instance.getClass());
            }
        } else {
            return new JavaField(instance, name);
        }
    }

}
