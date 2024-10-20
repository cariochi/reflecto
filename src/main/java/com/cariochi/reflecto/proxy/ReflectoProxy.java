package com.cariochi.reflecto.proxy;

import com.cariochi.reflecto.Reflecto;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReflectoProxy {

    public static ProxyType createTypeProxy(Type... types) {
        final List<ReflectoType> reflectoTypes = Stream.of(types).map(Reflecto::reflect).toList();
        final ReflectoType superClass = getSuperClass(reflectoTypes);
        final List<ReflectoType> interfaces = getInterfaces(reflectoTypes);
        return new ProxyType(superClass, interfaces);
    }

    private static ReflectoType getSuperClass(List<ReflectoType> types) {
        final List<ReflectoType> superClasses = types.stream()
                .filter(type -> !type.actualClass().isInterface())
                .toList();
        if (superClasses.size() > 1) {
            throw new IllegalArgumentException("Single super class allowed");
        }
        final Iterator<ReflectoType> iterator = superClasses.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    private static List<ReflectoType> getInterfaces(List<ReflectoType> types) {
        return types.stream()
                .filter(type -> type.actualClass().isInterface())
                .toList();
    }

}
