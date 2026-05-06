package com.cariochi.reflecto.methods;

import com.cariochi.reflecto.base.IsMethod;
import com.cariochi.reflecto.base.Streamable;
import com.cariochi.reflecto.exceptions.NotFoundException;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static com.cariochi.reflecto.utils.MethodsUtils.findMatchingMethod;
import static com.cariochi.reflecto.utils.MessageUtils.methods;
import static com.cariochi.reflecto.utils.MessageUtils.typeNames;
import static org.apache.commons.lang3.StringUtils.substringBefore;

/**
 * Streamable method collection with overload-aware lookup helpers.
 *
 * @param <T> method wrapper type
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class MethodsStreamable<T extends IsMethod> implements Streamable<T> {

    @Getter
    private final ReflectoType declaringType;

    private final Supplier<List<T>> listSupplier;

    @Getter(lazy = true)
    private final List<T> list = listSupplier.get();

    /**
     * Finds a method by name and argument types.
     * <p>
     * The name may be passed either as {@code "setName"} or as expression-style
     * {@code "setName(?)"}.
     *
     * @param name method name or method expression
     * @param argTypes argument types used for overload resolution
     * @return matching method, if present
     */
    public Optional<T> find(String name, Type... argTypes) {
        final String methodName = substringBefore(name, "(");
        final List<ReflectoType> argReflectoTypes = Stream.of(argTypes).map(declaringType::reflect).toList();
        return findMatchingMethod(list(), methodName, argReflectoTypes);
    }

    /**
     * Returns a method by name and argument types.
     *
     * @param name method name or method expression
     * @param argTypes argument types used for overload resolution
     * @return matching method
     * @throws NotFoundException when no method matches
     */
    public T get(String name, Type... argTypes) {
        return find(name, argTypes)
                .orElseThrow(() -> new NotFoundException("Method {0} with argument types {1} not found in {2}. Available methods named {3}: {4}",
                        substringBefore(name, "("),
                        typeNames(argTypes),
                        declaringType.name(),
                        substringBefore(name, "("),
                        methods(stream().filter(method -> substringBefore(name, "(").equals(method.name())))));
    }
}
