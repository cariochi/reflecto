package com.cariochi.reflecto.invocations.model;

import com.cariochi.reflecto.fields.TargetFields;
import com.cariochi.reflecto.invocations.Invocations;
import com.cariochi.reflecto.methods.TargetMethods;
import com.cariochi.reflecto.types.ReflectoType;
import java.util.List;

/**
 * Reflection wrapper around a runtime value.
 * <p>
 * A {@code Reflection} can read or update the wrapped value directly, navigate
 * through nested fields and methods, or expose target-bound field and method
 * collections.
 *
 * <pre>{@code
 * Reflection bug = Reflecto.reflect(bugInstance);
 *
 * String username = bug.perform("reporter.username");
 * bug.perform("watchers[0].username=?", "java-dev");
 * bug.reflect("reporter.username").setValue("qa-lead");
 * }</pre>
 */
public interface Reflection {

    /**
     * Returns the resolved type of the wrapped value.
     *
     * @return value type
     */
    ReflectoType type();

    /**
     * Reads the wrapped value.
     *
     * @param <V> expected value type
     * @return wrapped value
     */
    <V> V getValue();

    /**
     * Updates the wrapped value when the reflection represents a mutable target,
     * such as a field, list item, array item, or map value.
     *
     * @param value new value
     * @param <V> value type
     */
    <V> void setValue(V value);

    /**
     * Resolves an expression against the wrapped value and returns another
     * reflection.
     * <p>
     * Expressions may combine field access, method invocation, collection/map
     * indexing, assignment placeholders, and wildcard iteration.
     *
     * @param expression expression to resolve
     * @param args values for {@code ?} placeholders
     * @return resolved reflection, or a composite reflection for wildcard reads
     */
    default Reflection reflect(String expression, Object... args) {
        final List<Reflection> reflections = Invocations.parse(expression, args).apply(getValue(), type());
        return expression.contains("[*]") ? new CompositeReflection(reflections) : reflections.get(0);
    }

    /**
     * Resolves an expression and returns its value.
     *
     * @param expression expression to resolve
     * @param args values for {@code ?} placeholders
     * @param <V> expected result type
     * @return resolved value
     */
    default <V> V perform(String expression, Object... args) {
        return reflect(expression, args).getValue();
    }

    /**
     * Returns target-bound fields for the wrapped object.
     *
     * @return fields bound to the current value
     */
    default TargetFields fields() {
        return new TargetFields(type().fields(), getValue());
    }

    /**
     * Returns target-bound methods for the wrapped object.
     *
     * @return methods bound to the current value
     */
    default TargetMethods methods() {
        return new TargetMethods(type().methods(), getValue());
    }

}
