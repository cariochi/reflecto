package com.cariochi.reflecto.base;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Common collection contract used by Reflecto metadata views.
 *
 * @param <T> element type
 */
public interface Streamable<T> extends Iterable<T> {

    /**
     * Returns the materialized list.
     *
     * @return elements as a list
     */
    List<T> list();

    /**
     * Returns a stream over {@link #list()}.
     *
     * @return element stream
     */
    default Stream<T> stream() {
        return list().stream();
    }

    /**
     * Returns an element by index.
     *
     * @param index element index
     * @return element at index
     */
    default T get(int index) {
        return list().get(index);
    }

    /**
     * Returns the number of elements.
     *
     * @return number of elements
     */
    default long size() {
        return list().size();
    }

    /**
     * Checks whether there are no elements.
     *
     * @return {@code true} when empty
     */
    default boolean isEmpty() {
        return list().isEmpty();
    }

    /**
     * Returns an iterator over {@link #list()}.
     */
    default Iterator<T> iterator() {
        return list().iterator();
    }


}
