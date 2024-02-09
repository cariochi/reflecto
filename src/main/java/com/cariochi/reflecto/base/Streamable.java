package com.cariochi.reflecto.base;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public interface Streamable<T> extends Iterable<T> {

    List<T> list();

    default Stream<T> stream() {
        return list().stream();
    }

    default T get(int index) {
        return list().get(index);
    }

    default long size() {
        return list().size();
    }

    default boolean isEmpty() {
        return list().isEmpty();
    }

    default Iterator<T> iterator() {
        return list().iterator();
    }


}
