package com.cariochi.reflecto.base;

import java.lang.reflect.Modifier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class ReflectoModifiers {

    private final int value;

    public boolean isPublic() {
        return Modifier.isPublic(value);
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(value);
    }

    public boolean isProtected() {
        return Modifier.isProtected(value);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(value);
    }

    public boolean isStatic() {
        return Modifier.isStatic(value);
    }

    public boolean isFinal() {
        return Modifier.isFinal(value);
    }

    public boolean isTransient() {
        return Modifier.isTransient(value);
    }

    public boolean isInterface() {
        return Modifier.isInterface(value);
    }

    public boolean isStrict() {
        return Modifier.isStrict(value);
    }

    public boolean isNative() {
        return Modifier.isNative(value);
    }

    public boolean isSynchronized() {
        return Modifier.isSynchronized(value);
    }

    public boolean isVolatile() {
        return Modifier.isVolatile(value);
    }

}
