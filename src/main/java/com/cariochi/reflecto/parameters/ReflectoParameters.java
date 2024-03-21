package com.cariochi.reflecto.parameters;

import com.cariochi.reflecto.base.Streamable;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Executable;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class ReflectoParameters implements Streamable<ReflectoParameter> {

    private final Executable executable;

    private final ReflectoType declaringType;

    @Getter(lazy = true)
    private final List<ReflectoParameter> list = Stream.of(executable.getParameters())
            .map(declaringType::reflect)
            .collect(toList());

    @Getter(lazy = true)
    private final List<ReflectoType> types = stream().map(ReflectoParameter::type).collect(toList());
}
