package com.cariochi.reflecto.proxy;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import javassist.util.proxy.ProxyObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class ProxyFactory {

    @SneakyThrows
    public static <T> T createInstance(MethodHandler methodHandler, Class<?>... types) {
        final javassist.util.proxy.ProxyFactory factory = new javassist.util.proxy.ProxyFactory();
        factory.setSuperclass(getSuperClass(types));
        final List<Class<?>> interfaces = getInterfaces(types);
        if (!interfaces.isEmpty()) {
            factory.setInterfaces(interfaces.toArray(Class<?>[]::new));
        }
        final Class<?> proxyClass = factory.createClass();
        final T proxyInstance = (T) proxyClass.newInstance();
        ((ProxyObject) proxyInstance).setHandler(new ProxyMethodHandler(methodHandler));
        return proxyInstance;

    }

    private static List<Class<?>> getInterfaces(Class<?>[] types) {
        return Stream.of(types).filter(Class::isInterface).collect(toList());
    }

    private static Class<?> getSuperClass(Class<?>[] types) {
        final List<Class<?>> superClasses = Stream.of(types).filter(t -> !t.isInterface()).collect(toList());
        if (superClasses.size() > 1) {
            throw new IllegalArgumentException("Single super class allowed");
        }
        final Iterator<Class<?>> iterator = superClasses.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }


    public interface MethodHandler {

        Object invoke(Object proxy, Method method, Object[] args, MethodProceed proceed) throws Throwable;

    }

    public interface MethodProceed {

        Object proceed() throws Throwable;

    }

    @RequiredArgsConstructor
    private static class ProxyMethodHandler implements javassist.util.proxy.MethodHandler {

        private final MethodHandler handler;

        @Override
        public Object invoke(Object proxy, Method method, Method proceed, Object[] args) throws Throwable {
            return handler.invoke(proxy, method, args, proceed == null ? null : () -> proceed.invoke(proxy, args));
        }

    }

}
