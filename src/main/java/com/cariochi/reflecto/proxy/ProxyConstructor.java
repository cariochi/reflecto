package com.cariochi.reflecto.proxy;

import com.cariochi.reflecto.constructors.ReflectoConstructor;
import com.cariochi.reflecto.types.ReflectoType;
import java.util.function.Supplier;
import javassist.util.proxy.ProxyObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Constructor facade for generated proxy classes.
 */
@RequiredArgsConstructor
public class ProxyConstructor {

    private final ReflectoType proxyType;
    private final ReflectoConstructor proxyTypeConstructor;
    private final ReflectoConstructor handlerConstructor;
    private final Supplier<? extends InvocationHandler> handlerSupplier;

    /**
     * Creates a proxy instance and installs the configured invocation handler.
     *
     * @param args constructor arguments for the proxy and, when applicable, the handler
     * @param <T> expected proxy type
     * @return proxy instance
     */
    @SneakyThrows
    public <T> T newInstance(Object... args) {
        final T proxyInstance = proxyTypeConstructor.newInstance(args);
        ((ProxyObject) proxyInstance).setHandler(new MethodHandlerWrapper(proxyType, getInvocationHandler(args)));
        return proxyInstance;
    }

    private InvocationHandler getInvocationHandler(Object[] args) {
        if (handlerSupplier != null) {
            return handlerSupplier.get();
        }
        return handlerConstructor.parameters().isEmpty()
                ? handlerConstructor.newInstance()
                : handlerConstructor.newInstance(args);
    }
}
