package com.cariochi.reflecto.invocations;

import com.cariochi.reflecto.Reflecto;
import com.cariochi.reflecto.exceptions.NotFoundException;
import com.cariochi.reflecto.fields.TargetField;
import com.cariochi.reflecto.invocations.model.ArrayField;
import com.cariochi.reflecto.invocations.model.ListField;
import com.cariochi.reflecto.invocations.model.MapField;
import com.cariochi.reflecto.invocations.model.NullReflection;
import com.cariochi.reflecto.invocations.model.Reflection;
import com.cariochi.reflecto.types.ReflectoObject;
import com.cariochi.reflecto.types.ReflectoType;
import com.cariochi.reflecto.utils.ExpressionUtils;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.cariochi.reflecto.utils.CollectionUtils.queueOf;
import static com.cariochi.reflecto.utils.MessageUtils.typeNames;
import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.ClassUtils.toClass;
import static org.apache.commons.lang3.StringUtils.substringBefore;

class Invocation {

    private final String expression;
    private final Object[] args;
    private final boolean isMethod;

    public Invocation(String expression, List<Object> args) {
        this.isMethod = expression.contains("(");
        this.expression = substringBefore(expression, "(");
        this.args = args.toArray();
    }

    public List<Reflection> apply(Object instance, ReflectoType type) {

        if (instance == null) {
            return List.of(new NullReflection());
        }

        if (isMethod) {
            final Class<?>[] argTypes = toClass(args);
            return type.methods().find(expression, argTypes)
                    .map(matchingMethod -> matchingMethod.withTarget(instance))
                    .map(method -> new ReflectoObject(method.invoke(args), method.returnType()))
                    .map(Reflection.class::cast)
                    .map(List::of)
                    .orElseThrow(() -> new NotFoundException(
                            "Method {0} with argument types {1} not found while resolving expression {2} on type {3}",
                            expression,
                            typeNames(argTypes),
                            expression,
                            type.name()));
        }

        if (expression.equals("[*]")) {
            return applyComposite(instance, type);
        } else {
            Queue<Object> argsQueue = queueOf(args);
            final Reflection field = getField(instance, type, argsQueue);
            if (!argsQueue.isEmpty()) {
                field.setValue(argsQueue.poll());
            }
            return List.of(field);
        }
    }

    private Reflection getField(Object instance, ReflectoType type, Queue<Object> argsQueue) {
        if (expression.startsWith("[")) {
            Object key = ExpressionUtils.parseKey(expression);
            if ("?".equals(key)) {
                key = argsQueue.poll();
                if (key == null) {
                    throw new IllegalArgumentException("Missing value for indexed expression " + expression);
                }
            }
            if (type.isArray()) {
                final ReflectoType itemType = type.asArray().componentType();
                return new ArrayField((Object[]) instance, parseIndex(key), itemType);
            } else if (type.is(List.class)) {
                final ReflectoType itemType = type.as(Iterable.class).arguments().get(0);
                return new ListField((List<Object>) instance, parseIndex(key), itemType);
            } else if (type.is(Map.class)) {
                final ReflectoType valueType = type.as(Map.class).arguments().get(1);
                return new MapField((Map<Object, Object>) instance, key, valueType);
            } else {
                throw new IllegalArgumentException("Cannot access expression " + expression + " by index on type " + type.name());
            }
        } else {
            return type.fields().find(expression)
                    .map(field -> new TargetField(instance, field))
                    .map(Reflection.class::cast)
                    .orElseGet(NullReflection::new);
        }
    }

    private List<Reflection> applyComposite(Object instance, ReflectoType type) {

        if (type.isArray()) {
            final Object[] array = (Object[]) instance;
            return getAllFromArray(array, type.asArray().componentType());
        }

        if (type.is(Map.class)) {
            final Map<Object, Object> map = (Map<Object, Object>) instance;
            return getAllFromMap(map, type.as(Map.class).arguments().get(1));
        }

        if (type.is(List.class)) {
            final List<Object> list = (List<Object>) instance;
            return getAllFromList(list, type.as(Iterable.class).arguments().get(0));
        }

        if (type.is(Set.class)) {
            final Set<Object> set = (Set<Object>) instance;
            return getAllFromSet(set, type.as(Iterable.class).arguments().get(0));
        }

        if (type.is(Iterable.class)) {
            if (args.length != 0) {
                throw new IllegalArgumentException("Cannot assign through wildcard expression %s on read-only iterable type %s".formatted(expression, type.name()));
            } else {
                final Iterable<?> iterable = (Iterable<?>) instance;
                ReflectoType itemType = type.as(Iterable.class).arguments().get(0);
                return getAllFromIterable(iterable, itemType);
            }
        }

        throw new IllegalArgumentException("Cannot iterate wildcard expression %s on type %s".formatted(expression, type.name()));
    }

    private static int parseIndex(Object key) {
        try {
            return parseInt(key.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Index must be an integer, but was " + key, e);
        }
    }

    private List<Reflection> getAllFromArray(Object[] array, ReflectoType componentType) {
        return IntStream.range(0, array.length)
                .mapToObj(i -> new ArrayField(array, i, componentType))
                .peek(field -> {
                    if (args.length != 0) {
                        field.setValue(args[0]);
                    }
                })
                .map(Reflection.class::cast)
                .toList();
    }

    private List<Reflection> getAllFromMap(Map<Object, Object> map, ReflectoType valueType) {
        return map.keySet().stream()
                .map(o -> new MapField(map, o, valueType))
                .peek(field -> {
                    if (args.length != 0) {
                        field.setValue(args[0]);
                    }
                })
                .map(Reflection.class::cast)
                .toList();
    }

    private List<Reflection> getAllFromList(List<Object> list, ReflectoType itemType) {
        return IntStream.range(0, list.size())
                .mapToObj(i -> new ListField(list, i, itemType))
                .peek(field -> {
                    if (args.length != 0) {
                        field.setValue(args[0]);
                    }
                })
                .map(Reflection.class::cast)
                .toList();
    }

    private List<Reflection> getAllFromSet(Set<Object> set, ReflectoType itemType) {
        if (args.length != 0) {
            final int size = set.size();
            set.clear();
            return IntStream.range(0, size)
                    .peek(i -> set.add(args[0]))
                    .mapToObj(i -> new ReflectoObject(args[0], itemType))
                    .map(Reflection.class::cast)
                    .toList();
        } else {
            return set.stream()
                    .map(Reflecto::reflect)
                    .toList();
        }
    }

    private static List<Reflection> getAllFromIterable(Iterable<?> iterable, ReflectoType itemType) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(item -> new ReflectoObject(item, itemType))
                .map(Reflection.class::cast)
                .toList();
    }

}
