package com.cariochi.reflecto.utils;

import java.util.LinkedList;
import java.util.Queue;
import lombok.experimental.UtilityClass;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@UtilityClass
public class CollectionUtils {

    public static <T> Queue<T> queueOf(T... items) {
        return new LinkedList<>(items == null ? emptyList() : asList(items));
    }

}
