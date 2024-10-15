package com.cariochi.reflecto.methods;

import com.cariochi.reflecto.base.Streamable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class TargetMethods implements Streamable<TargetMethod> {

    private final ReflectoMethods methods;

    @Getter
    private final Object target;

    @Getter(lazy = true)
    private final List<TargetMethod> list = methods.stream().map(m -> m.withTarget(target)).collect(toList());

    public Optional<TargetMethod> find(String name, Type... argTypes) {
        return methods.find(name, argTypes)
                .map(m -> m.withTarget(target));
    }

    public TargetMethod get(String name, Type... argTypes) {
        return methods.get(name, argTypes).withTarget(target);
    }
}
