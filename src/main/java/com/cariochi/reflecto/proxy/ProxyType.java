package com.cariochi.reflecto.proxy;

import com.cariochi.reflecto.types.ReflectoType;
import com.cariochi.reflecto.types.TypeName;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javassist.bytecode.SignatureAttribute.ClassSignature;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import static com.cariochi.reflecto.Reflecto.reflect;
import static com.cariochi.reflecto.utils.SignatureUtils.createClassSignature;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

/**
 * Builder for a generated proxy type.
 * <p>
 * A proxy type may have one superclass and any number of interfaces. Generic
 * signatures are preserved when parameterized {@link java.lang.reflect.Type}
 * instances are used.
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class ProxyType {

    private final ReflectoType superType;
    private final List<ReflectoType> interfaces;

    @Getter(lazy = true)
    private final ReflectoType type = createProxyType();

    /**
     * Uses an invocation handler class.
     * <p>
     * The handler class constructor must match the proxy constructor selected
     * later with {@code getConstructor(...)} unless it has a no-argument
     * constructor.
     *
     * @param handlerClass invocation handler implementation class
     * @return proxy factory
     */
    @SneakyThrows
    public ProxyFactory with(Class<? extends InvocationHandler> handlerClass) {
        return new ProxyFactory(type(), reflect(handlerClass), null);
    }

    /**
     * Uses an invocation handler supplier.
     *
     * @param handlerSupplier invocation handler supplier
     * @return proxy factory
     */
    @SneakyThrows
    public ProxyFactory with(Supplier<? extends InvocationHandler> handlerSupplier) {
        return new ProxyFactory(type(), null, handlerSupplier);
    }

    private ReflectoType createProxyType() {
        final javassist.util.proxy.ProxyFactory factory = new javassist.util.proxy.ProxyFactory();
        factory.setUseCache(false);
        Optional.ofNullable(superType).map(ReflectoType::actualClass).ifPresent(factory::setSuperclass);
        if (isNotEmpty(interfaces)) {
            factory.setInterfaces(interfaces.stream().map(ReflectoType::actualClass).toArray(Class<?>[]::new));
        }
        factory.setGenericSignature(getClassSignature().encode());
        return reflect(factory.createClass());
    }

    private ClassSignature getClassSignature() {

        final TypeName superObjectType = Optional.ofNullable(superType)
                .map(ReflectoType::actualType)
                .map(TypeName::of)
                .orElse(null);

        final List<TypeName> interfaceTypes = Optional.ofNullable(interfaces).stream()
                .flatMap(List::stream)
                .map(ReflectoType::actualType)
                .map(TypeName::of)
                .toList();

        return createClassSignature(superObjectType, interfaceTypes);
    }

}
