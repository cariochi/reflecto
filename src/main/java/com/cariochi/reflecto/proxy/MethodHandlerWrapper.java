package com.cariochi.reflecto.proxy;

import com.cariochi.reflecto.methods.ReflectoMethod;
import com.cariochi.reflecto.methods.TargetMethod;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.cariochi.reflecto.Reflecto.reflect;

@RequiredArgsConstructor
public class MethodHandlerWrapper implements javassist.util.proxy.MethodHandler {

    private final ReflectoType proxyType;

    @Getter
    private final InvocationHandler handler;

    @Override
    public Object invoke(Object proxy, Method method, Method proceed, Object[] args) throws Throwable {

        final Class<?> declaringClass = method.getDeclaringClass();

        final TargetMethod handlerMethodImpl = reflect(handler).methods().find(method.getName(), method.getParameterTypes()).orElse(null);
        if (handlerMethodImpl != null) {
            final ReflectoType proxyDeclaringType = findDeclaringClass(proxyType, declaringClass).orElseThrow();
            final ReflectoType handlerDeclaringType = findDeclaringClass(reflect(handler.getClass()), declaringClass).orElse(null);
            if (proxyDeclaringType.equals(handlerDeclaringType)) {
                if (proceed == null) {
                    return handlerMethodImpl.invoke(args);
                } else if (!handlerMethodImpl.declaringType().equals(proxyDeclaringType)) {
                    final Object[] arguments = Stream.of(args).map(arg -> arg == proxy ? handler : proxy).toArray();
                    return handlerMethodImpl.invoke(arguments);
                }
            }
        }

        final ReflectoMethod thisMethod = proxyType.reflect(method);
        final TargetMethod proceedMethod = proceed == null ? null : proxyType.reflect(proceed).withTarget(proxy);
        return handler.invoke(proxy, thisMethod, args, proceedMethod);
    }

    private Optional<ReflectoType> findDeclaringClass(ReflectoType reflectoType, Class<?> declaringClass) {
        return Stream.of(Stream.of(reflectoType), reflectoType.allSuperTypes().stream(), reflectoType.allInterfaces().stream())
                .flatMap(Function.identity())
                .filter(type -> declaringClass.equals(type.actualClass()))
                .findFirst();
    }

}
