package com.cariochi.reflecto.methods;

import com.cariochi.reflecto.base.Streamable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static com.cariochi.reflecto.utils.MethodsUtils.findMatchingMethod;
import static org.apache.commons.lang3.StringUtils.substringBefore;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class ReflectoMethods implements Streamable<ReflectoMethod> {

    private final Supplier<List<ReflectoMethod>> listSupplier;

    @Getter(lazy = true)
    private final List<ReflectoMethod> list = listSupplier.get();

    public Optional<ReflectoMethod> find(String name, Class<?>... argClasses) {
        final String methodName = substringBefore(name, "(");
        return findMatchingMethod(list(), methodName, argClasses);
    }

}
