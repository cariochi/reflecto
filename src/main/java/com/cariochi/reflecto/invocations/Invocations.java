package com.cariochi.reflecto.invocations;

import com.cariochi.reflecto.invocations.model.Reflection;
import com.cariochi.reflecto.types.ReflectoObject;
import com.cariochi.reflecto.types.ReflectoType;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import lombok.RequiredArgsConstructor;

import static com.cariochi.reflecto.utils.CollectionUtils.queueOf;
import static com.cariochi.reflecto.utils.ExpressionUtils.splitExpression;
import static java.util.stream.IntStream.range;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.countMatches;
import static org.apache.commons.lang3.StringUtils.substringBefore;


@RequiredArgsConstructor(access = PRIVATE)
public class Invocations {

    private final List<Invocation> invocations;

    public static Invocations parse(String expression, Object... args) {
        final Queue<Object> params = queueOf(args);
        final List<Invocation> invocations = splitExpression(expression).stream()
                .map(exp -> new Invocation(
                        substringBefore(exp, "=").trim(),
                        range(0, countMatches(exp, '?')).mapToObj(i -> params.poll()).toList())
                )
                .toList();
        return new Invocations(invocations);
    }

    public List<Reflection> apply(Object instance, ReflectoType type) {
        final Iterator<Invocation> iterator = invocations.iterator();
        List<Reflection> reflections = List.of(new ReflectoObject(instance, type));
        while (iterator.hasNext()) {
            final Invocation nextInvocation = iterator.next();
            reflections = reflections.stream()
                    .flatMap(reflection -> nextInvocation.apply(reflection.getValue(), reflection.type()).stream())
                    .toList();
        }
        return reflections;
    }

}
