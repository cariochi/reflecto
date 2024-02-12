package com.cariochi.reflecto.methods;

import com.cariochi.reflecto.base.Streamable;
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

    private final Object target;

    @Getter(lazy = true)
    private final List<TargetMethod> list = methods.stream().map(m -> m.withTarget(target)).collect(toList());

    public Optional<TargetMethod> find(String name, Class<?>... argClasses) {
        return methods.find(name, argClasses)
                .map(m -> m.withTarget(target));
    }

}
