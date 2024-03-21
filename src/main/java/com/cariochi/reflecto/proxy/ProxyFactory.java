package com.cariochi.reflecto.proxy;

import com.cariochi.reflecto.constructors.ReflectoConstructor;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

import static com.cariochi.reflecto.Reflecto.reflect;
import static java.lang.String.format;
import static org.apache.commons.lang3.reflect.ConstructorUtils.getMatchingAccessibleConstructor;

@RequiredArgsConstructor
public class ProxyFactory {

    private final ReflectoType proxyType;
    private final ReflectoType handlerType;
    private final Supplier<? extends InvocationHandler> handlerSupplier;

    public ProxyConstructor getConstructor(Class<?>... paramTypes) {

        final Constructor<?> proxyTypeConstructor = getMatchingAccessibleConstructor(proxyType.actualClass(), paramTypes);
        if (proxyTypeConstructor == null) {
            throw new IllegalArgumentException(format(
                    "Constructor of `%s` type with parameters %s not found",
                    proxyType.superType().actualType().getTypeName(),
                    Arrays.toString(paramTypes)
            ));
        }

        return new ProxyConstructor(proxyType, reflect(proxyTypeConstructor), handlerConstructor(paramTypes), handlerSupplier);
    }

    private ReflectoConstructor handlerConstructor(Class<?>[] paramTypes) {
        if (handlerSupplier != null) {
            return null;
        }
        return handlerType.declared().constructors().find(paramTypes)
                .orElseThrow(() -> new IllegalArgumentException(format(
                        "Constructor of `%s` type with parameters %s not found",
                        handlerType.actualType().getTypeName(),
                        Arrays.toString(paramTypes)
                )));
    }

}
