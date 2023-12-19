package com.cariochi.reflecto.proxy;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.SneakyThrows;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import static java.lang.invoke.MethodType.methodType;

@RequiredArgsConstructor
@Builder
public class ProxyFactory {

    private final Class<?> extendsClass;

    @Singular
    private final List<Class<?>> implementsInterfaces;

    private final Supplier<InvocationHandler> methodInterceptor;

    public <T> T create() {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(extendsClass);
        if (!implementsInterfaces.isEmpty()) {
            enhancer.setInterfaces(implementsInterfaces.toArray(Class<?>[]::new));
        }
        enhancer.setCallback(new ProxyMethodInterceptor(methodInterceptor.get()));
        return (T) enhancer.create();
    }

    public interface MethodPostProcessor {

        Object postProcess(Object proxy, Method method, Object[] args, Object result);

    }

    @RequiredArgsConstructor
    private static class ProxyMethodInterceptor implements MethodInterceptor {

        private final InvocationHandler handler;

        @Override
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if (Modifier.isAbstract(method.getModifiers())) {
                return handler.invoke(proxy, method, args);
            } else if (method.getDeclaringClass().equals(Object.class)) {
                return invokeObjectMethod(proxy, method, args);
            } else {
                final Object result = invokeDefaultMethod(proxy, method, args, methodProxy);
                if (handler instanceof ProxyFactory.MethodPostProcessor) {
                    return ((MethodPostProcessor) handler).postProcess(proxy, method, args, result);
                } else {
                    return result;
                }
            }
        }

        @SneakyThrows
        private Object invokeDefaultMethod(Object proxy, Method method, Object[] args, MethodProxy methodProxy) {
            if (method.isDefault()) {
                final MethodType methodType = methodType(method.getReturnType(), method.getParameterTypes());
                return MethodHandles.lookup()
                        .findSpecial(method.getDeclaringClass(), method.getName(), methodType, method.getDeclaringClass())
                        .bindTo(proxy)
                        .invokeWithArguments(args);
            } else {
                return methodProxy.invokeSuper(proxy, args);
            }
        }

        @SneakyThrows
        private Object invokeObjectMethod(Object proxy, Method method, Object[] args) {
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] == proxy) {
                        args[i] = this;
                    }
                }
            }
            return method.invoke(this, args);
        }

    }

}
