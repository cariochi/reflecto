package com.cariochi.reflecto.methods;

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
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.substringBefore;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class ReflectoMethods implements Streamable<ReflectoMethod> {

    private final ReflectoType declaringType;

    private final Supplier<List<ReflectoMethod>> listSupplier;

    @Getter(lazy = true)
    private final List<ReflectoMethod> list = listSupplier.get();

    public Optional<ReflectoMethod> find(String name, Type... argTypes) {
        final String methodName = substringBefore(name, "(");
        final List<ReflectoType> argReflectoTypes = Stream.of(argTypes).map(declaringType::reflect).collect(toList());
        return findMatchingMethod(list(), methodName, argReflectoTypes);
    }

    public ReflectoMethod get(String name, Type... argTypes) {
        return find(name, argTypes)
                .orElseThrow(() -> new NotFoundException("Method {0} with argument types {1} not found", name, Arrays.toString(argTypes)));
    }
}
