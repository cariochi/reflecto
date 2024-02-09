package com.cariochi.reflecto.constructors;

import com.cariochi.reflecto.base.Streamable;
import com.cariochi.reflecto.types.ReflectoType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class ReflectoConstructors implements Streamable<ReflectoConstructor> {

    private final ReflectoType declaredType;

    @Getter(lazy = true)
    private final List<ReflectoConstructor> list = Stream.of(declaredType.actualClass().getConstructors())
            .map(constructor -> new ReflectoConstructor(constructor, declaredType))
            .collect(toList());

    @SneakyThrows
    public Optional<ReflectoConstructor> find(Class<?>... parameterTypes) {
        return Optional.ofNullable(declaredType.actualClass().getConstructor(parameterTypes))
                .map(constructor -> new ReflectoConstructor(constructor, declaredType));
    }

}
