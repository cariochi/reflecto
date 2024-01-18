package com.cariochi.reflecto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import static com.cariochi.reflecto.Reflecto.reflect;
import static com.cariochi.reflecto.utils.CollectionUtils.queueOf;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.countMatches;
import static org.apache.commons.lang3.StringUtils.substringBefore;


@RequiredArgsConstructor(access = PRIVATE)
public class Invocations {

    private final List<Invocation> invocations;

    public static Invocations parse(String path, Object... args) {
        final Queue<Object> params = queueOf(args);
        final List<Invocation> invocations = stream(path.split("\\."))
                .flatMap(Invocations::splitByBrackets)
                .map(StringUtils::trim)
                .map(name -> new Invocation(
                        substringBefore(name, "=").trim(),
                        range(0, countMatches(name, '?')).mapToObj(i -> params.poll()).collect(toList()))
                )
                .collect(toList());
        return new Invocations(invocations);
    }

    public List<Reflection> apply(Object instance) {
        final Iterator<Invocation> iterator = invocations.iterator();
        List<Reflection> reflections = List.of(reflect(instance));
        while (iterator.hasNext()) {
            final Invocation nextInvocation = iterator.next();
            reflections = reflections.stream()
                    .flatMap(reflection -> nextInvocation.apply(reflection.getValue()).stream())
                    .collect(toList());
        }
        return reflections;
    }

    private static Stream<String> splitByBrackets(String s) {
        final List<String> list = new ArrayList<>();
        int start = 0;
        int index = s.indexOf('[');
        while (index != -1) {
            list.add(s.substring(start, index));
            start = index;
            index = s.indexOf("[", start + 1);
        }
        list.add(s.substring(start));
        return list.stream().filter(StringUtils::isNotBlank);
    }

}
