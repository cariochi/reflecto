package com.cariochi.reflecto.methods;

import com.cariochi.reflecto.base.IsMethod;
import com.cariochi.reflecto.base.Streamable;
import com.cariochi.reflecto.exceptions.NotFoundException;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static com.cariochi.reflecto.utils.MethodsUtils.findMatchingMethod;
import static org.apache.commons.lang3.StringUtils.substringBefore;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class MethodsStreamable<T extends IsMethod> implements Streamable<T> {

    @Getter
    private final ReflectoType declaringType;

    private final Supplier<List<T>> listSupplier;

    @Getter(lazy = true)
    private final List<T> list = listSupplier.get();

    public Optional<T> find(String name, Type... argTypes) {
        final String methodName = substringBefore(name, "(");
        final List<ReflectoType> argReflectoTypes = Stream.of(argTypes).map(declaringType::reflect).toList();
        return findMatchingMethod(list(), methodName, argReflectoTypes);
    }

    public T get(String name, Type... argTypes) {
        return find(name, argTypes)
                .orElseThrow(() -> new NotFoundException("Method {0} with argument types {1} not found", name, Arrays.toString(argTypes)));
    }
}
