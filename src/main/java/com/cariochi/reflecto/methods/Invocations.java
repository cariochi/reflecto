package com.cariochi.reflecto.methods;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.StringUtils.countMatches;


@UtilityClass
public class Invocations {

    public static List<Invocation> parse(String path, Object... args) {
        final Queue<Object> params = new LinkedList<>(List.of(args));
        return stream(path.split("\\."))
                .flatMap(Invocations::splitByBrackets)
                .map(name -> new Invocation(
                        name,
                        range(0, countMatches(name, '?')).mapToObj(i -> params.poll()).collect(toList()))
                )
                .collect(toList());
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
