package com.cariochi.reflecto.proxy;

import com.cariochi.reflecto.constructors.ReflectoConstructor;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.function.Supplier;
import javassist.util.proxy.ProxyObject;
import lombok.RequiredArgsConstructor;

import static com.cariochi.reflecto.Reflecto.reflect;
import static com.cariochi.reflecto.utils.MessageUtils.typeNames;
import static java.lang.String.format;
import static org.apache.commons.lang3.reflect.ConstructorUtils.getMatchingAccessibleConstructor;

/**
 * Factory for creating instances of a generated proxy type.
 */
@RequiredArgsConstructor
public class ProxyFactory {

    private final ReflectoType proxyType;
    private final ReflectoType handlerType;
    private final Supplier<? extends InvocationHandler> handlerSupplier;

    /**
     * Returns the Reflecto invocation handler installed on a proxy instance.
     *
     * @param proxy proxy instance
     * @return installed handler, if the object is a Reflecto proxy
     */
    public static Optional<InvocationHandler> getHandler(Object proxy) {
        return Optional.ofNullable(proxy)
                .filter(ProxyObject.class::isInstance)
                .map(ProxyObject.class::cast)
                .map(javassist.util.proxy.ProxyFactory::getHandler)
                .filter(MethodHandlerWrapper.class::isInstance)
                .map(MethodHandlerWrapper.class::cast)
                .map(MethodHandlerWrapper::getHandler);
    }

    /**
     * Selects a proxy constructor by parameter types.
     *
     * @param paramTypes constructor parameter types
     * @return proxy constructor facade
     */
    public ProxyConstructor getConstructor(Class<?>... paramTypes) {

        final Constructor<?> proxyTypeConstructor = getMatchingAccessibleConstructor(proxyType.actualClass(), paramTypes);
        if (proxyTypeConstructor == null) {
            throw new IllegalArgumentException("Constructor of `%s` type with parameter types %s not found".formatted(proxyType.superType().actualType().getTypeName(), typeNames(paramTypes)));
        }

        return new ProxyConstructor(proxyType, reflect(proxyTypeConstructor), handlerConstructor(paramTypes), handlerSupplier);
    }

    private ReflectoConstructor handlerConstructor(Class<?>[] paramTypes) {
        if (handlerSupplier != null) {
            return null;
        }
        return handlerType.constructors().declared().find(paramTypes)
                .orElseThrow(() -> new IllegalArgumentException(format(
                        "Constructor of `%s` type with parameter types %s not found",
                        handlerType.actualType().getTypeName(),
                        typeNames(paramTypes)
                )));
    }

}
