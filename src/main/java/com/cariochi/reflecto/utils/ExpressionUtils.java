package com.cariochi.reflecto.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.substringBetween;

@UtilityClass
public class ExpressionUtils {

    public static int parseIndex(String expression) {
        return Integer.parseInt(parseKey(expression));
    }

    public static String parseKey(String expression) {
        return substringBetween(expression, "[", "]");
    }

    public static List<String> splitExpression(String expression) {
        return stream(expression.split("\\."))
                .flatMap(ExpressionUtils::splitByBrackets)
                .map(StringUtils::trim)
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
