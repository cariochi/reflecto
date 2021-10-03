package com.cariochi.reflecto;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Stream;

import static com.cariochi.reflecto.Reflecto.reflect;
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
        final Queue<Object> params = new LinkedList<>(List.of(args));
        final List<Invocation> invocations = stream(path.split("\\."))
                .flatMap(Invocations::splitByBrackets)
                .map(name -> new Invocation(
                        substringBefore(name, "="),
                        range(0, countMatches(name, '?')).mapToObj(i -> params.poll()).collect(toList()))
                )
                .collect(toList());
        return new Invocations(invocations);
    }

    public Reflection applyAll(Object instance) {
        final Iterator<Invocation> iterator = invocations.iterator();
        Reflection reflection = reflect(instance);
        while (iterator.hasNext()) {
            reflection = iterator.next().apply(reflection.getValue());
        }
        return reflection;
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
