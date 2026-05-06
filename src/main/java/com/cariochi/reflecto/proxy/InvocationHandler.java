package com.cariochi.reflecto.proxy;

import com.cariochi.reflecto.methods.ReflectoMethod;
import com.cariochi.reflecto.methods.TargetMethod;

/**
 * Intercepts calls made to a Reflecto proxy.
 */
public interface InvocationHandler {

    /**
     * Handles a proxy method invocation.
     *
     * @param proxy proxy instance
     * @param thisMethod reflected method being invoked
     * @param args invocation arguments
     * @param proceed concrete method delegate, or {@code null} when the method has no implementation
     * @return invocation result
     * @throws Throwable any error raised by the handler or delegated method
     */
    Object invoke(Object proxy, ReflectoMethod thisMethod, Object[] args, TargetMethod proceed) throws Throwable;

}
