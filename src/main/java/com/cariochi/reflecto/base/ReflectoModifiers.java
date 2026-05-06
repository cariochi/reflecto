package com.cariochi.reflecto.base;

import java.lang.reflect.Modifier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Convenience wrapper around Java reflection modifier flags.
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class ReflectoModifiers {

    private final int value;

    /**
     * Checks for the public modifier.
     *
     * @return {@code true} when the public modifier is present
     */
    public boolean isPublic() {
        return Modifier.isPublic(value);
    }

    /**
     * Checks for the private modifier.
     *
     * @return {@code true} when the private modifier is present
     */
    public boolean isPrivate() {
        return Modifier.isPrivate(value);
    }

    /**
     * Checks for the protected modifier.
     *
     * @return {@code true} when the protected modifier is present
     */
    public boolean isProtected() {
        return Modifier.isProtected(value);
    }

    /**
     * Checks for the abstract modifier.
     *
     * @return {@code true} when the abstract modifier is present
     */
    public boolean isAbstract() {
        return Modifier.isAbstract(value);
    }

    /**
     * Checks for the static modifier.
     *
     * @return {@code true} when the static modifier is present
     */
    public boolean isStatic() {
        return Modifier.isStatic(value);
    }

    /**
     * Checks for the final modifier.
     *
     * @return {@code true} when the final modifier is present
     */
    public boolean isFinal() {
        return Modifier.isFinal(value);
    }

    /**
     * Checks for the transient modifier.
     *
     * @return {@code true} when the transient modifier is present
     */
    public boolean isTransient() {
        return Modifier.isTransient(value);
    }

    /**
     * Checks for the interface modifier.
     *
     * @return {@code true} when the interface modifier is present
     */
    public boolean isInterface() {
        return Modifier.isInterface(value);
    }

    /**
     * Checks for the strict modifier.
     *
     * @return {@code true} when the strict modifier is present
     */
    public boolean isStrict() {
        return Modifier.isStrict(value);
    }

    /**
     * Checks for the native modifier.
     *
     * @return {@code true} when the native modifier is present
     */
    public boolean isNative() {
        return Modifier.isNative(value);
    }

    /**
     * Checks for the synchronized modifier.
     *
     * @return {@code true} when the synchronized modifier is present
     */
    public boolean isSynchronized() {
        return Modifier.isSynchronized(value);
    }

    /**
     * Checks for the volatile modifier.
     *
     * @return {@code true} when the volatile modifier is present
     */
    public boolean isVolatile() {
        return Modifier.isVolatile(value);
    }

}
