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
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class ProxyType {

    private final ReflectoType superType;
    private final List<ReflectoType> interfaces;

    @Getter(lazy = true)
    private final ReflectoType type = createProxyType();

    @SneakyThrows
    public ProxyFactory with(Class<? extends InvocationHandler> handlerClass) {
        return new ProxyFactory(type(), reflect(handlerClass), null);
    }

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
                .collect(toList());

        return createClassSignature(superObjectType, interfaceTypes);
    }

}
